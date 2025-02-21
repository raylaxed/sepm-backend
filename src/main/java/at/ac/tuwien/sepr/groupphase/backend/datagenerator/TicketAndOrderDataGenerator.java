package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.entity.Order;
import at.ac.tuwien.sepr.groupphase.backend.entity.Seat;
import at.ac.tuwien.sepr.groupphase.backend.entity.Sector;
import at.ac.tuwien.sepr.groupphase.backend.entity.Show;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShowSector;
import at.ac.tuwien.sepr.groupphase.backend.entity.StandingSector;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ticket;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Objects;

import at.ac.tuwien.sepr.groupphase.backend.repository.OrderRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShowRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.EventRepository;

@Profile("generateData")
@Service
@DependsOn({"userDataGenerator", "hallDataGenerator", "venueDataGenerator", "showDataGenerator"})
public class TicketAndOrderDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final OrderRepository orderRepository;
    private final ShowRepository showRepository;
    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private final EventRepository eventRepository;

    public TicketAndOrderDataGenerator(OrderRepository orderRepository, ShowRepository showRepository,
                                     UserRepository userRepository, TicketRepository ticketRepository,
                                     EventRepository eventRepository) {
        this.orderRepository = orderRepository;
        this.showRepository = showRepository;
        this.userRepository = userRepository;
        this.ticketRepository = ticketRepository;
        this.eventRepository = eventRepository;
    }

    @Transactional
    @PostConstruct
    public void generateOrders() {
        if (orderRepository.count() > 0) {
            LOGGER.debug("orders already generated");
            return;
        }

        List<Show> shows = showRepository.findAllWithShowSectors();
        List<ApplicationUser> users = userRepository.findAll();
        List<Show> showsToUpdate = new ArrayList<>();
        List<Event> eventsToUpdate = new ArrayList<>();

        if (shows.isEmpty() || users.isEmpty()) {
            LOGGER.error("No shows or users found in database");
            return;
        }

        // Collect all sectors that need their seats loaded
        Set<Sector> sectors = shows.stream()
            .flatMap(show -> show.getShowSectors().stream())
            .map(ShowSector::getSector)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        // Load all sectors with their seats in a separate query
        List<Sector> sectorsWithSeats = showRepository.findSectorsWithSeats(sectors);

        // Create a map for quick sector lookup
        Map<Long, Sector> sectorMap = sectorsWithSeats.stream()
            .collect(Collectors.toMap(Sector::getId, s -> s));

        // Update the sectors in shows with the fully loaded ones
        for (Show show : shows) {
            for (ShowSector ss : show.getShowSectors()) {
                if (ss.getSector() != null) {
                    ss.setSector(sectorMap.get(ss.getSector().getId()));
                }
            }
        }

        Random random = new Random();

        // Track used seats per show using a composite key of show_id and seat_id
        Map<String, Boolean> usedSeatsMap = new HashMap<>();
        Map<Show, Map<StandingSector, Integer>> usedStandingCapacityPerShow = new HashMap<>();

        // Generate 1000-1400 orders for purchased tickets
        int numberOfOrders = 1100;

        // Generate additional 200-400 reservations and cart items
        int numberOfReservations = 350;

        // Lists for batch saving
        List<Order> ordersToSave = new ArrayList<>();
        List<Ticket> ticketsToSave = new ArrayList<>();

        // Process both purchased tickets and reservations
        generateTickets(shows, users, random, usedSeatsMap, usedStandingCapacityPerShow,
            numberOfOrders, true, ordersToSave, ticketsToSave, showsToUpdate, eventsToUpdate);
        generateTickets(shows, users, random, usedSeatsMap, usedStandingCapacityPerShow,
            numberOfReservations, false, ordersToSave, ticketsToSave, showsToUpdate, eventsToUpdate);

        // Batch save all entities
        LOGGER.debug("Saving {} orders", ordersToSave.size());
        orderRepository.saveAll(ordersToSave);

        LOGGER.debug("Saving {} tickets", ticketsToSave.size());
        ticketRepository.saveAll(ticketsToSave);

        LOGGER.debug("Updating {} shows", showsToUpdate.size());
        showRepository.saveAll(showsToUpdate);

        LOGGER.debug("Updating {} events", eventsToUpdate.size());
        eventRepository.saveAll(eventsToUpdate);
    }

    private void generateTickets(List<Show> shows, List<ApplicationUser> users, Random random,
                               Map<String, Boolean> usedSeatsMap,
                               Map<Show, Map<StandingSector, Integer>> usedStandingCapacityPerShow,
                               int count, boolean forPurchase,
                               List<Order> ordersToSave, List<Ticket> ticketsToSave,
                               List<Show> showsToUpdate, List<Event> eventsToUpdate) {

        for (int i = 0; i < count; i++) {
            Show show = shows.get(random.nextInt(shows.size()));
            final ApplicationUser user = users.get(random.nextInt(users.size()));

            usedStandingCapacityPerShow.putIfAbsent(show, new HashMap<>());

            List<ShowSector> showSectors = show.getShowSectors();
            if (showSectors.isEmpty()) {
                continue;
            }

            int numTickets = random.nextInt(4) + 1;
            List<Ticket> orderTickets = new ArrayList<>();
            double totalPrice = 0;

            boolean groupOrder = random.nextDouble() < 0.3;
            ShowSector groupSector = null;
            if (groupOrder) {
                groupSector = showSectors.get(random.nextInt(showSectors.size()));
            }

            for (int j = 0; j < numTickets; j++) {
                ShowSector showSector = groupOrder ? groupSector : showSectors.get(random.nextInt(showSectors.size()));
                Ticket ticket = null;

                if (showSector.getSector() != null) {
                    List<Seat> availableSeats = showSector.getSector().getSeats().stream()
                        .filter(seat -> !isShowSeatTaken(show, seat, usedSeatsMap))
                        .collect(Collectors.toList());

                    if (!availableSeats.isEmpty()) {
                        Seat selectedSeat = availableSeats.get(random.nextInt(availableSeats.size()));
                        String seatKey = createShowSeatKey(show, selectedSeat);

                        if (!usedSeatsMap.containsKey(seatKey)) {
                            usedSeatsMap.put(seatKey, true);
                            ticket = createSeatedTicket(show, user, showSector, selectedSeat);
                        }
                    }
                } else {
                    StandingSector standingSector = showSector.getStandingSector();
                    int used = usedStandingCapacityPerShow.get(show)
                        .getOrDefault(standingSector, 0);

                    if (used < standingSector.getCapacity()) {
                        ticket = createStandingTicket(show, user, showSector, standingSector);
                        usedStandingCapacityPerShow.get(show)
                            .put(standingSector, used + 1);
                    }
                }

                if (ticket != null) {
                    if (forPurchase) {
                        ticket.setPurchased(true);
                        ticket.setReserved(false);
                        ticket.setInCart(false);
                    } else {
                        // 60% chance for reservation, 40% chance for cart
                        boolean isReserved = random.nextDouble() < 0.6;
                        ticket.setPurchased(false);
                        ticket.setReserved(isReserved);
                        ticket.setInCart(!isReserved);
                    }
                    orderTickets.add(ticket);
                    totalPrice += showSector.getPrice();
                }
            }

            if (!orderTickets.isEmpty()) {
                if (forPurchase) {
                    // Round total price to 2 decimal places
                    double roundedTotal = Math.round(totalPrice * 100.0) / 100.0;

                    Order order = Order.OrderBuilder.anOrder()
                        .withOrderDate(LocalDateTime.now().minusDays(random.nextInt(180)))
                        .withUser(user)
                        .withTotal(roundedTotal)
                        .withPaymentIntentId("pi_3Qm0qkKB0xHb61lm1sWzfeDf")
                        .build();

                    ordersToSave.add(order);

                    // Update soldSeats counters only for purchased tickets
                    int ticketCount = orderTickets.size();
                    show.setSoldSeats(show.getSoldSeats() + ticketCount);
                    if (!showsToUpdate.contains(show)) {
                        showsToUpdate.add(show);
                    }

                    if (show.getEvent() != null) {
                        Event event = show.getEvent();
                        event.setSoldSeats(event.getSoldSeats() + ticketCount);
                        if (!eventsToUpdate.contains(event)) {
                            eventsToUpdate.add(event);
                        }
                    }

                    for (Ticket ticket : orderTickets) {
                        ticket.setOrder(order);
                        ticketsToSave.add(ticket);
                    }
                } else {
                    // For reservations and cart items, just collect the tickets
                    ticketsToSave.addAll(orderTickets);
                }
            }
        }
    }

    private String createShowSeatKey(Show show, Seat seat) {
        return show.getId() + "_" + seat.getSeatId();
    }

    private boolean isShowSeatTaken(Show show, Seat seat, Map<String, Boolean> usedSeatsMap) {
        return usedSeatsMap.containsKey(createShowSeatKey(show, seat));
    }


    private Ticket createSeatedTicket(Show show, ApplicationUser user, ShowSector showSector, Seat seat) {
        return Ticket.TicketBuilder.aTicket()
            .withShow(show)
            .withUser(user)
            .withPrice(showSector.getPrice())
            .withTicketType("REGULAR")
            .withSeat(seat)
            .withPurchased(true)
            .withReserved(false)
            .withInCart(false)
            .withDate(LocalDateTime.now())
            .build();
    }

    private Ticket createStandingTicket(Show show, ApplicationUser user, ShowSector showSector, StandingSector standingSector) {
        return Ticket.TicketBuilder.aTicket()
            .withShow(show)
            .withUser(user)
            .withPrice(showSector.getPrice())
            .withTicketType("STANDING")
            .withStandingSector(standingSector)
            .withPurchased(true)
            .withReserved(false)
            .withInCart(false)
            .withDate(LocalDateTime.now())
            .build();
    }
}