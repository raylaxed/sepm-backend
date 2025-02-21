package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.entity.Order;
import at.ac.tuwien.sepr.groupphase.backend.entity.Seat;
import at.ac.tuwien.sepr.groupphase.backend.entity.Show;
import at.ac.tuwien.sepr.groupphase.backend.entity.StandingSector;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepr.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.ArrayList;

import at.ac.tuwien.sepr.groupphase.backend.service.PdfGenerationService;
import at.ac.tuwien.sepr.groupphase.backend.service.TicketService;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.FatalException;

import org.hibernate.Hibernate;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShowRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.SeatRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.HallRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.EventRepository;

import java.io.OutputStream;
import org.springframework.data.util.Pair;

@Service
@Transactional
public class SimpleTicketService implements TicketService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final PdfGenerationService pdfGenerationService;
    private final OrderRepository orderRepository;
    private final ShowRepository showRepository;
    private final SeatRepository seatRepository;
    private final HallRepository hallRepository;
    private final EventRepository eventRepository;


    @Autowired
    public SimpleTicketService(TicketRepository ticketRepository, UserRepository userRepository,
        PdfGenerationService pdfGenerationService, OrderRepository orderRepository, ShowRepository showRepository,
        SeatRepository seatRepository, HallRepository hallRepository, EventRepository eventRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.pdfGenerationService = pdfGenerationService;
        this.orderRepository = orderRepository;
        this.showRepository = showRepository;
        this.seatRepository = seatRepository;
        this.hallRepository = hallRepository;
        this.eventRepository = eventRepository;
    }

    private boolean isShowInPast(Show show) {
        LOGGER.info("Checking if show {} is in past. Show time: {}", show.getId(), show.getTime());

        if (show.getTime() == null) {
            LOGGER.info("Show {} has null time value", show.getId());
            Show freshShow = showRepository.findById(show.getId())
                .orElseThrow(() -> new NotFoundException("Show not found"));
            if (freshShow.getTime() == null) {
                throw new IllegalArgumentException("Show time cannot be null for show with ID: " + show.getId());
            }
            show = freshShow;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime showEndTime = LocalDateTime.of(
            show.getDate(),
            show.getTime().plusMinutes(show.getDuration())
        );
        return now.isAfter(showEndTime);
    }

    @Override
    public List<Ticket> createTickets(List<Ticket> tickets) throws ConflictException {
        final List<Ticket> conflictingTickets = new ArrayList<>();

        // Check if any shows are in the past (including duration)
        if (tickets.stream().anyMatch(ticket -> isShowInPast(ticket.getShow()))) {
            throw new ConflictException("Ticket creation failed",
                List.of("Cannot create tickets for past shows"));
        }

        // Check if any ticket is already purchased
        if (tickets.stream().anyMatch(Ticket::getPurchased)) {
            throw new ConflictException("Ticket creation failed",
                List.of("The ticket cannot be purchased"));
        }

        // Check for reserved and inCart status
        for (Ticket ticket : tickets) {
            boolean isReserved = ticket.getReserved() != null && ticket.getReserved();
            boolean isInCart = ticket.getInCart() != null && ticket.getInCart();

            if (isReserved && isInCart) {
                throw new ConflictException("Ticket creation failed",
                    List.of("A ticket cannot be both reserved and in the cart"));
            }

            if (!isReserved && !isInCart) {
                throw new ConflictException("Ticket creation failed",
                    List.of("A ticket must be either reserved or in the cart"));
            }
        }

        // Check for capacity
        // Group new tickets by StandingSector ID AND Show ID for capacity checking
        Map<Pair<Long, Long>, Long> newTicketsPerSectorAndShow = tickets.stream()
            .filter(t -> "STANDING".equals(t.getTicketType()) && t.getStandingSector() != null && t.getShow() != null)
            .collect(Collectors.groupingBy(
                t -> Pair.of(t.getStandingSector().getId(), t.getShow().getId()),
                Collectors.counting()));

        for (Map.Entry<Pair<Long, Long>, Long> entry : newTicketsPerSectorAndShow.entrySet()) {
            Long sectorId = entry.getKey().getFirst();
            Long showId = entry.getKey().getSecond();
            Long newTicketCount = entry.getValue();

            // Fetch the StandingSector to get its capacity
            StandingSector sector = hallRepository.findStandingSectorById(sectorId)
                .orElseThrow(() -> new NotFoundException("StandingSector with ID " + sectorId + " not found"));

            Integer capacity = sector.getCapacity();

            // Count existing tickets in this sector AND show that are inCart, reserved, or purchased
            Long existingTicketCount = ticketRepository.countByStandingSectorIdAndShowIdAndInCartTrueOrReservedTrueOrPurchasedTrue(sectorId, showId);

            if ((existingTicketCount + newTicketCount) > capacity) {
                List<Ticket> newConflictingTickets = tickets.stream()
                    .filter(t -> "STANDING".equals(t.getTicketType()) 
                        && t.getStandingSector() != null 
                        && t.getShow() != null
                        && t.getStandingSector().getId().equals(sectorId)
                        && t.getShow().getId().equals(showId))
                    .collect(Collectors.toList());
                conflictingTickets.addAll(newConflictingTickets);
            }
        }

        tickets.forEach(ticket -> {
            ticket.setOrder(null);
            ticket.setDate(LocalDateTime.now());

            // Handle user reference
            if (ticket.getUser() != null && ticket.getUser().getId() != null) {
                ticket.setUser(userRepository.findById(ticket.getUser().getId())
                    .orElse(null));
            }

            // Handle show reference and update both Show and Event soldSeats
            if (ticket.getShow() != null && ticket.getShow().getId() != null) {
                Show managedShow = showRepository.findById(ticket.getShow().getId())
                    .orElse(null);
                if (managedShow != null) {
                    // Update Show soldSeats
                    managedShow.setSoldSeats(managedShow.getSoldSeats() + 1);
                    showRepository.save(managedShow);

                    // Update Event soldSeats
                    Event event = managedShow.getEvent();
                    if (event != null) {
                        event.setSoldSeats(event.getSoldSeats() + 1);
                        eventRepository.save(event);
                    }
                }
                ticket.setShow(managedShow);
            }

            // Handle ticket type specific references
            if ("STANDING".equals(ticket.getTicketType())) {
                // For standing tickets, ensure seat is null and standing sector is managed
                ticket.setSeat(null);
                if (ticket.getStandingSector() != null && ticket.getStandingSector().getId() != null) {
                    StandingSector managedSector = hallRepository.findStandingSectorById(
                        ticket.getStandingSector().getId()).orElse(null);
                    ticket.setStandingSector(managedSector);
                }
            } else if ("REGULAR".equals(ticket.getTicketType())) {
                // For regular tickets, ensure standing sector is null and seat is managed
                ticket.setStandingSector(null);
                if (ticket.getSeat() != null && ticket.getSeat().getSeatId() != null) {
                    Seat managedSeat = seatRepository.findById(ticket.getSeat().getSeatId())
                        .orElse(null);
                    ticket.setSeat(managedSeat);

                    // Check for existing tickets with the same seat and show
                    List<Ticket> existingTickets = ticketRepository.findAll().stream()
                        .filter(t -> t.getSeat() != null
                            && t.getSeat().getSeatId().equals(managedSeat.getSeatId())
                            && t.getShow().getId().equals(ticket.getShow().getId())
                            && (t.getInCart() || t.getReserved() || t.getPurchased()))
                        .toList();

                    if (!existingTickets.isEmpty()) {
                        conflictingTickets.addAll(existingTickets);
                    }
                }
            }
        });

        if (!conflictingTickets.isEmpty()) {
            // If there are conflicts, rollback both Show and Event soldSeats increments
            tickets.forEach(ticket -> {
                if (ticket.getShow() != null && ticket.getShow().getId() != null) {
                    Show managedShow = showRepository.findById(ticket.getShow().getId())
                        .orElse(null);
                    if (managedShow != null) {
                        // Rollback Show soldSeats
                        managedShow.setSoldSeats(managedShow.getSoldSeats() - 1);
                        showRepository.save(managedShow);

                        // Rollback Event soldSeats
                        Event event = managedShow.getEvent();
                        if (event != null) {
                            event.setSoldSeats(event.getSoldSeats() - 1);
                            eventRepository.save(event);
                        }
                    }
                }
            });
            throw new ConflictException(
                "Ticket creation failed",
                List.of("One or more seats are already taken for this show")
            );
        }

        return ticketRepository.saveAll(tickets);
    }

    public List<Ticket> getTicketsByUser(ApplicationUser user) {
        LOGGER.info("getting tickets by user in service");
        return ticketRepository.findByUser(user);
    }


    @Override
    public List<Ticket> addToCart(List<Long> ticketIds, Long userId) throws ConflictException {
        LOGGER.debug("Adding tickets with ids: {} to cart for user with id: {}", ticketIds, userId);
        List<Ticket> tickets = ticketRepository.findAllById(ticketIds);

        // Check if all tickets exist
        if (tickets.size() != ticketIds.size()) {
            throw new NotFoundException("One or more tickets not found");
        }

        // Check if any tickets are already reserved by another user
        if (tickets.stream().anyMatch(ticket ->
            ticket.getReserved() && !userId.equals(ticket.getUser().getId()))) {
            throw new ConflictException("Adding tickets to cart failed",
                List.of("One or more tickets are already reserved by someone else"));
        }

        // Check if any tickets are already in another cart
        if (tickets.stream().anyMatch(ticket ->
            ticket.getInCart() && !userId.equals(ticket.getUser().getId()))) {
            throw new ConflictException("Adding tickets to cart failed",
                List.of("One or more tickets are already reserved by someone else"));
        }

        // Check if any tickets are already purchased
        if (tickets.stream().anyMatch(Ticket::getPurchased)) {
            throw new ConflictException("Adding tickets to cart failed",
                List.of("One or more tickets are already reserved by someone else"));
        }

        // Update cart status
        for (Ticket ticket : tickets) {
            ticket.setDate(LocalDateTime.now());
            ticket.setInCart(true);
            ticket.setReserved(false);
            ticket.setPurchased(false);
            ticket.setUser(userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found")));
        }

        return ticketRepository.saveAll(tickets);
    }

    @Override
    public List<Ticket> purchaseTickets(List<Long> ticketIds, Long userId) throws ConflictException {
        LOGGER.debug("Purchasing tickets with ids: {} for user with id: {}", ticketIds, userId);
        List<Ticket> tickets = ticketRepository.findAllById(ticketIds);

        // Check if all tickets exist
        if (tickets.size() != ticketIds.size()) {
            throw new NotFoundException("One or more tickets not found");
        }

        // Check if any shows are in the past (including duration)
        if (tickets.stream().anyMatch(ticket -> isShowInPast(ticket.getShow()))) {
            throw new ConflictException("Purchasing tickets failed",
                List.of("Cannot purchase tickets for past shows"));
        }

        // Check if all tickets are in the cart
        if (tickets.stream().anyMatch(ticket -> !ticket.getInCart())) {
            throw new ConflictException("Purchasing tickets failed",
                List.of("One or more tickets are not in the cart"));
        }

        // Check if any tickets are already reserved
        if (tickets.stream().anyMatch(Ticket::getReserved)) {
            throw new ConflictException("Purchasing tickets failed",
                List.of("One or more tickets are already reserved"));
        }

        // Check if any tickets are already purchased
        if (tickets.stream().anyMatch(Ticket::getPurchased)) {
            throw new ConflictException("Purchasing tickets failed",
                List.of("One or more tickets are already reserved"));
        }

        // Update purchased flag
        for (Ticket ticket : tickets) {
            ticket.setPurchased(true);
            ticket.setReserved(false);
            ticket.setInCart(false);
            ticket.setUser(userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found")));
        }

        // Save changes
        return ticketRepository.saveAll(tickets);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ticket> getUserTickets(Long userId) throws NotFoundException {
        ApplicationUser user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User not found"));
        return ticketRepository.findByUser(user);
    }

    @Override
    public void cancelTicketReservation(Long ticketId, Long userId) throws ConflictException {
        ApplicationUser user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User not found"));

        // Use Optional to find the ticket
        ticketRepository.findByIdAndUser(ticketId, user).ifPresent(ticket -> {
            // Only delete the ticket if it is reserved
            if (ticket.getReserved()) {
                decreaseShowEventSoldSeats(ticket);
                ticketRepository.delete(ticket);
            } else {
                throw new NotFoundException("Ticket is not reserved");
            }
        });
    }

    @Override
    public void removeFromCart(Long ticketId) throws NotFoundException {
        // Attempt to find the ticket by ID
        ticketRepository.findById(ticketId).ifPresent(ticket -> {
            // Only delete the ticket if it is in the cart
            if (ticket.getInCart()) {
                // Handle show reference and update both Show and Event soldSeats
                decreaseShowEventSoldSeats(ticket);

                ticketRepository.delete(ticket);
            } else {
                throw new NotFoundException("Ticket not found in cart");
            }
        });
    }

    @Override
    public Order cancelPurchasedTickets(List<Long> ticketIds, Long userId) throws ConflictException, NotFoundException {
        LOGGER.debug("Canceling purchased tickets with ids: {} for user with id: {}", ticketIds, userId);
        List<Ticket> tickets = ticketRepository.findAllById(ticketIds);

        // Check if all tickets exist
        if (tickets.size() != ticketIds.size()) {
            throw new NotFoundException("One or more tickets not found");
        }

        // Check if any tickets are not purchased
        if (tickets.stream().anyMatch(ticket -> !ticket.getPurchased())) {
            throw new ConflictException("Canceling purchase failed",
                List.of("One or more tickets are not purchased"));
        }

        // Log the retrieved tickets
        tickets.forEach(ticket -> LOGGER.info("Retrieved ticket: {}", ticket));

        // Ensure all tickets have an associated order
        if (tickets.stream().anyMatch(ticket -> ticket.getOrder() == null)) {
            throw new ConflictException("Tickets are not associated with an order", List.of("Tickets must have an associated order"));
        }

        // Retrieve the order associated with the tickets
        Long orderId = tickets.get(0).getOrder().getId();
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new NotFoundException("Order not found for the given tickets"));

        // Update purchased flag
        for (Ticket ticket : tickets) {
            ticket.setPurchased(false);
            ticket.setReserved(false);
            ticket.setInCart(false);
            decreaseShowEventSoldSeats(ticket);
        }

        // Save changes
        ticketRepository.saveAll(tickets);

        // Save changes
        return order;
    }

    private void decreaseShowEventSoldSeats(Ticket ticket) {
        // Handle show reference and update both Show and Event soldSeats
        if (ticket.getShow() != null && ticket.getShow().getId() != null) {
            Show managedShow = showRepository.findById(ticket.getShow().getId())
                .orElse(null);
            if (managedShow != null) {
                // Update Show soldSeats
                managedShow.setSoldSeats(managedShow.getSoldSeats() - 1);
                showRepository.save(managedShow);

                // Update Event soldSeats
                Event event = managedShow.getEvent();
                if (event != null) {
                    event.setSoldSeats(event.getSoldSeats() - 1);
                    eventRepository.save(event);
                }
            }
            ticket.setShow(managedShow);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ticket> getReservedTickets(Long userId) throws NotFoundException {
        ApplicationUser user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User not found"));
        Hibernate.initialize(user.getReservedTickets());
        return user.getReservedTickets();
    }

    @Override
    public void generatePdfForTicket(Long ticketId, Long userId, OutputStream outputStream)
        throws NotFoundException, ConflictException {
        LOGGER.debug("Generating PDF for ticket with id {} for user {}", ticketId, userId);

        // Find the ticket
        Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new NotFoundException(String.format("Could not find ticket with id %d", ticketId)));

        // Check if the user owns the ticket
        if (ticket.getUser() == null || !ticket.getUser().getId().equals(userId)) {
            throw new ConflictException("Access denied",
                List.of("You are not authorized to access this ticket"));
        }

        // Check if the ticket is actually purchased or reserved
        if (!ticket.getPurchased() && !ticket.getReserved()) {
            throw new ConflictException("Invalid ticket state",
                List.of("The ticket must be purchased or reserved to generate PDF"));
        }

        try {
            pdfGenerationService.generateTicketPdf(ticket, outputStream);
        } catch (Exception e) {
            throw new FatalException("Failed to generate PDF for ticket", e);
        }
    }

    @Override
    public List<Ticket> getTicketsByOrder(Long orderId) {
        LOGGER.debug("Getting tickets for order {}", orderId);
        return ticketRepository.findByOrderId(orderId);
    }
}