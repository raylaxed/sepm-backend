package at.ac.tuwien.sepr.groupphase.backend.unittests;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Order;
import at.ac.tuwien.sepr.groupphase.backend.entity.Seat;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepr.groupphase.backend.entity.Show;
import at.ac.tuwien.sepr.groupphase.backend.entity.StandingSector;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.HallRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.OrderRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.SeatRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShowRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.SimpleTicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class TicketServiceTest {

    @Autowired
    private SimpleTicketService ticketService;

    @MockBean
    private TicketRepository ticketRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private OrderRepository orderRepository;

    @MockBean
    private HallRepository hallRepository;

    @MockBean
    private SeatRepository seatRepository;

    @MockBean
    private ShowRepository showRepository;

    private Ticket ticket;
    private Order order;
    private ApplicationUser user;
    private Show show;

    @BeforeEach
    void setUp() {
        user = new ApplicationUser();
        user.setId(1L);

        show = new Show();
        show.setId(1L);
        show.setDate(LocalDate.now().plusDays(1));
        show.setTime(LocalTime.now());
        show.setDuration(120);

        ticket = new Ticket();
        ticket.setId(1L);
        ticket.setPurchased(true);
        ticket.setShow(show);
        ticket.setUser(user);

        order = new Order();
        order.setId(1L);
        ticket.setOrder(order);
    }

    @Test
    void givenValidTicketIds_whenCancelPurchasedTickets_thenSuccess() throws ConflictException, NotFoundException {
        // Arrange
        when(ticketRepository.findAllById(List.of(1L))).thenReturn(List.of(ticket));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // Act
        Order result = ticketService.cancelPurchasedTickets(List.of(1L), 1L);

        // Assert
        assertNotNull(result);
        assertEquals(order.getId(), result.getId());
        verify(ticketRepository, times(1)).saveAll(anyList());
    }

    @Test
    void givenValidTickets_whenCreateTickets_thenSuccess() throws ConflictException {
        Ticket newTicket = new Ticket();
        newTicket.setShow(show);
        newTicket.setInCart(true);

        when(ticketRepository.saveAll(anyList())).thenReturn(List.of(newTicket));

        List<Ticket> result = ticketService.createTickets(List.of(newTicket));

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(ticketRepository, times(1)).saveAll(anyList());
    }

    @Test
    void givenValidUser_whenGetTicketsByUser_thenSuccess() {
        when(ticketRepository.findByUser(user)).thenReturn(List.of(ticket));

        List<Ticket> result = ticketService.getTicketsByUser(user);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(ticketRepository, times(1)).findByUser(user);
    }

    @Test
    void givenValidTicketIdsAndUser_whenAddToCart_thenSuccess() throws ConflictException {
        Ticket cartTicket = new Ticket();
        cartTicket.setShow(show);
        cartTicket.setInCart(false);

        when(ticketRepository.findAllById(List.of(1L))).thenReturn(List.of(cartTicket));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(ticketRepository.saveAll(anyList())).thenReturn(List.of(cartTicket));

        List<Ticket> result = ticketService.addToCart(List.of(1L), 1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void givenValidTicketIdsAndUser_whenPurchaseTickets_thenSuccess() throws ConflictException {
        Ticket purchaseTicket = new Ticket();
        purchaseTicket.setShow(show);
        purchaseTicket.setInCart(true);

        when(ticketRepository.findAllById(List.of(1L))).thenReturn(List.of(purchaseTicket));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(ticketRepository.saveAll(anyList())).thenReturn(List.of(purchaseTicket));

        List<Ticket> result = ticketService.purchaseTickets(List.of(1L), 1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(ticketRepository, times(1)).saveAll(anyList());
    }

    @Test
    void givenValidUserId_whenGetUserTickets_thenSuccess() throws NotFoundException {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(ticketRepository.findByUser(user)).thenReturn(List.of(ticket));

        // Act
        List<Ticket> result = ticketService.getUserTickets(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(ticketRepository, times(1)).findByUser(user);
    }

    @Test
    void givenValidTicketId_whenRemoveFromCart_thenSuccess() {
        Ticket cartTicket = new Ticket();
        cartTicket.setInCart(true);

        when(ticketRepository.findById(1L)).thenReturn(Optional.of(cartTicket));
        doNothing().when(ticketRepository).delete(cartTicket);

        assertDoesNotThrow(() -> ticketService.removeFromCart(1L));
        verify(ticketRepository, times(1)).delete(cartTicket);
    }

    @Test
    void givenValidUserId_whenGetReservedTickets_thenSuccess() throws NotFoundException {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        user.setReservedTickets(List.of(ticket));

        List<Ticket> result = ticketService.getReservedTickets(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void givenValidOrderId_whenGetTicketsByOrder_thenSuccess() {
        when(ticketRepository.findByOrderId(1L)).thenReturn(List.of(ticket));

        List<Ticket> result = ticketService.getTicketsByOrder(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(ticketRepository, times(1)).findByOrderId(1L);
    }

    @Test
    void givenInvalidTicketIds_whenCancelPurchasedTickets_thenThrowNotFoundException() {
        // Arrange
        when(ticketRepository.findAllById(List.of(1L))).thenReturn(List.of());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> ticketService.cancelPurchasedTickets(List.of(1L), 1L));
    }

    @Test
    void givenValidTicketIdAndUserId_whenCancelTicketReservation_thenSuccess() throws ConflictException, NotFoundException {
        // Arrange
        ApplicationUser user = new ApplicationUser();
        user.setId(1L);
        Ticket ticket = new Ticket();
        ticket.setId(1L);
        ticket.setReserved(true);
        ticket.setUser(user);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(ticketRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(ticket));

        // Act
        ticketService.cancelTicketReservation(1L, 1L);

        // Assert
        verify(ticketRepository, times(1)).delete(ticket);
    }

    @Test
    void givenInvalidTicketId_whenCancelTicketReservation_thenThrowNotFoundException() {
        // Arrange
        ApplicationUser user = new ApplicationUser();
        user.setId(1L);
        Ticket ticket = new Ticket();
        ticket.setId(1L);
        ticket.setReserved(false);
        ticket.setUser(user);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(ticketRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(ticket));

        // Act & Assert
        assertThrows(NotFoundException.class, () -> ticketService.cancelTicketReservation(1L, 1L));
    }

    @Test
    void givenPastShow_whenCreateTickets_thenThrowConflictException() {
        // Arrange
        Show pastShow = new Show();
        pastShow.setId(1L);
        pastShow.setDate(LocalDate.now().minusDays(1));
        pastShow.setTime(LocalTime.now());
        pastShow.setDuration(120);

        Ticket pastTicket = new Ticket();
        pastTicket.setShow(pastShow);
        pastTicket.setInCart(true);

        // Act & Assert
        ConflictException exception = assertThrows(ConflictException.class,
            () -> ticketService.createTickets(List.of(pastTicket)));
        assertTrue(exception.getMessage().contains("Cannot create tickets for past shows"));
    }

    @Test
    void givenAlreadyPurchasedTicket_whenCreateTickets_thenThrowConflictException() {
        // Arrange
        Ticket purchasedTicket = new Ticket();
        purchasedTicket.setShow(show);
        purchasedTicket.setPurchased(true);
        purchasedTicket.setInCart(true);

        // Act & Assert
        ConflictException exception = assertThrows(ConflictException.class,
            () -> ticketService.createTickets(List.of(purchasedTicket)));
        assertTrue(exception.getMessage().contains("The ticket cannot be purchased"));
    }

    @Test
    void givenTicketBothReservedAndInCart_whenCreateTickets_thenThrowConflictException() {
        // Arrange
        Ticket conflictingTicket = new Ticket();
        conflictingTicket.setShow(show);
        conflictingTicket.setReserved(true);
        conflictingTicket.setInCart(true);

        // Act & Assert
        ConflictException exception = assertThrows(ConflictException.class,
            () -> ticketService.createTickets(List.of(conflictingTicket)));
        assertTrue(exception.getMessage().contains("A ticket cannot be both reserved and in the cart"));
    }

    @Test
    void givenTicketNeitherReservedNorInCart_whenCreateTickets_thenThrowConflictException() {
        // Arrange
        Ticket invalidTicket = new Ticket();
        invalidTicket.setShow(show);
        invalidTicket.setReserved(false);
        invalidTicket.setInCart(false);

        // Act & Assert
        ConflictException exception = assertThrows(ConflictException.class,
            () -> ticketService.createTickets(List.of(invalidTicket)));
        assertTrue(exception.getMessage().contains("A ticket must be either reserved or in the cart"));
    }

    @Test
    void givenExceededSectorCapacity_whenCreateTickets_thenThrowConflictException() {
        // Arrange
        StandingSector sector = new StandingSector();
        sector.setId(1L);
        sector.setCapacity(10);

        Ticket standingTicket = new Ticket();
        standingTicket.setShow(show);
        standingTicket.setInCart(true);
        standingTicket.setTicketType("STANDING");
        standingTicket.setStandingSector(sector);

        when(hallRepository.findStandingSectorById(1L)).thenReturn(Optional.of(sector));
        when(ticketRepository.countByStandingSectorIdAndShowIdAndInCartTrueOrReservedTrueOrPurchasedTrue(1L, 1L))
            .thenReturn(10L); // Sector is already at capacity

        // Act & Assert
        ConflictException exception = assertThrows(ConflictException.class,
            () -> ticketService.createTickets(List.of(standingTicket)));
        assertTrue(exception.getMessage().contains("One or more seats are already taken for this show"));
    }

    @Test
    void givenDuplicateSeatBooking_whenCreateTickets_thenThrowConflictException() {
        // Arrange
        Seat seat = new Seat();
        seat.setSeatId(1L);

        Ticket regularTicket = new Ticket();
        regularTicket.setShow(show);
        regularTicket.setInCart(true);
        regularTicket.setTicketType("REGULAR");
        regularTicket.setSeat(seat);

        Ticket existingTicket = new Ticket();
        existingTicket.setShow(show);
        existingTicket.setSeat(seat);
        existingTicket.setPurchased(true);

        when(seatRepository.findById(1L)).thenReturn(Optional.of(seat));
        when(ticketRepository.findAll()).thenReturn(List.of(existingTicket));
        when(showRepository.findById(1L)).thenReturn(Optional.of(show));

        // Act & Assert
        ConflictException exception = assertThrows(ConflictException.class,
            () -> ticketService.createTickets(List.of(regularTicket)));
        assertTrue(exception.getMessage().contains("One or more seats are already taken for this show"));
    }

    @Test
    void givenNonExistentStandingSector_whenCreateTickets_thenThrowNotFoundException() {
        // Arrange
        StandingSector sector = new StandingSector();
        sector.setId(1L);

        Ticket standingTicket = new Ticket();
        standingTicket.setShow(show);
        standingTicket.setInCart(true);
        standingTicket.setTicketType("STANDING");
        standingTicket.setStandingSector(sector);

        when(hallRepository.findStandingSectorById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class,
            () -> ticketService.createTickets(List.of(standingTicket)));
    }
}