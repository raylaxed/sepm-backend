package at.ac.tuwien.sepr.groupphase.backend.unittests;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TicketDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.TicketMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Order;
import at.ac.tuwien.sepr.groupphase.backend.entity.Seat;
import at.ac.tuwien.sepr.groupphase.backend.entity.Show;
import at.ac.tuwien.sepr.groupphase.backend.entity.StandingSector;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ticket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TicketMappingTest {

    private TicketMapper ticketMapper;

    @BeforeEach
    void setUp() {
        ticketMapper = Mappers.getMapper(TicketMapper.class);
    }

    @Test
    void givenTicket_whenMapToTicketDto_thenDtoHasAllProperties() {
        // Arrange
        Ticket ticket = TestData.createTestTicket();
        Show show = new Show();
        show.setId(100L);
        ticket.setShow(show);

        StandingSector standingSector = new StandingSector();
        standingSector.setId(200L);
        ticket.setStandingSector(standingSector);

        Seat seat = new Seat();
        seat.setSeatId(300L);
        ticket.setSeat(seat);

        ApplicationUser user = new ApplicationUser();
        user.setId(400L);
        ticket.setUser(user);

        Order order = new Order();
        order.setId(500L);
        ticket.setOrder(order);

        ticket.setTicketType("Regular");
        ticket.setPrice(50.0);
        ticket.setReserved(true);
        ticket.setPurchased(false);
        ticket.setInCart(false);
        ticket.setDate(LocalDateTime.now());

        // Act
        TicketDto ticketDto = ticketMapper.ticketToTicketDto(ticket);

        // Assert
        assertNotNull(ticketDto);
        assertAll(
            () -> assertEquals(ticket.getId(), ticketDto.getId()),
            () -> assertEquals(show.getId(), ticketDto.getShowId()),
            () -> assertEquals(standingSector.getId(), ticketDto.getStandingSectorId()),
            () -> assertEquals(seat.getSeatId(), ticketDto.getSeatId()),
            () -> assertEquals(ticket.getPrice(), ticketDto.getPrice()),
            () -> assertEquals(ticket.getReserved(), ticketDto.getReserved()),
            () -> assertEquals(ticket.getPurchased(), ticketDto.getPurchased()),
            () -> assertEquals(ticket.getInCart(), ticketDto.getInCart()),
            () -> assertEquals(ticket.getDate(), ticketDto.getDate()),
            () -> assertEquals(user.getId(), ticketDto.getUserId()),
            () -> assertEquals(order.getId(), ticketDto.getOrderId()),
            () -> assertEquals(ticket.getTicketType(), ticketDto.getTicketType())
        );
    }

    @Test
    void givenTicketDto_whenMapToTicket_thenEntityHasAllProperties() {
        // Arrange
        TicketDto ticketDto = new TicketDto();
        ticketDto.setId(1L);
        ticketDto.setShowId(100L);
        ticketDto.setStandingSectorId(200L);
        ticketDto.setSeatId(300L);
        ticketDto.setPrice(50.0);
        ticketDto.setReserved(true);
        ticketDto.setPurchased(false);
        ticketDto.setInCart(false);
        ticketDto.setDate(LocalDateTime.now());
        ticketDto.setUserId(400L);
        ticketDto.setOrderId(500L);
        ticketDto.setTicketType("Regular");

        // Act
        Ticket ticket = ticketMapper.ticketDtoToTicket(ticketDto);

        // Assert
        assertNotNull(ticket);
        assertAll(
            () -> assertEquals(ticketDto.getId(), ticket.getId()),
            () -> assertNotNull(ticket.getShow()),
            () -> assertEquals(ticketDto.getShowId(), ticket.getShow().getId()),
            () -> assertNotNull(ticket.getStandingSector()),
            () -> assertEquals(ticketDto.getStandingSectorId(), ticket.getStandingSector().getId()),
            () -> assertNotNull(ticket.getSeat()),
            () -> assertEquals(ticketDto.getSeatId(), ticket.getSeat().getSeatId()),
            () -> assertEquals(ticketDto.getPrice(), ticket.getPrice()),
            () -> assertEquals(ticketDto.getReserved(), ticket.getReserved()),
            () -> assertEquals(ticketDto.getPurchased(), ticket.getPurchased()),
            () -> assertEquals(ticketDto.getInCart(), ticket.getInCart()),
            () -> assertEquals(ticketDto.getDate(), ticket.getDate()),
            () -> assertNotNull(ticket.getUser()),
            () -> assertEquals(ticketDto.getUserId(), ticket.getUser().getId()),
            () -> assertNotNull(ticket.getOrder()),
            () -> assertEquals(ticketDto.getOrderId(), ticket.getOrder().getId()),
            () -> assertEquals(ticketDto.getTicketType(), ticket.getTicketType())
        );
    }

    @Test
    void givenListOfTickets_whenMapToListOfTicketDtos_thenDtosHaveAllProperties() {
        // Arrange
        Ticket ticket1 = TestData.createTestTicket();
        Ticket ticket2 = TestData.createTestTicket();
        List<Ticket> tickets = List.of(ticket1, ticket2);

        // Act
        List<TicketDto> ticketDtos = ticketMapper.ticketToTicketDto(tickets);

        // Assert
        assertNotNull(ticketDtos);
        assertEquals(2, ticketDtos.size());

        TicketDto dto1 = ticketDtos.get(0);
        TicketDto dto2 = ticketDtos.get(1);

        assertAll(
            () -> assertEquals(ticket1.getId(), dto1.getId()),
            () -> assertEquals(ticket2.getId(), dto2.getId())
        );
    }

    @Test
    void givenListOfTicketDtos_whenMapToListOfTickets_thenEntitiesHaveAllProperties() {
        // Arrange
        TicketDto ticketDto1 = new TicketDto();
        ticketDto1.setId(1L);
        ticketDto1.setShowId(100L);
        ticketDto1.setTicketType("Regular");

        TicketDto ticketDto2 = new TicketDto();
        ticketDto2.setId(2L);
        ticketDto2.setShowId(101L);
        ticketDto2.setTicketType("VIP");

        List<TicketDto> ticketDtos = List.of(ticketDto1, ticketDto2);

        // Act
        List<Ticket> tickets = ticketMapper.ticketDtoToTicket(ticketDtos);

        // Assert
        assertNotNull(tickets);
        assertEquals(2, tickets.size());

        Ticket ticket1 = tickets.get(0);
        Ticket ticket2 = tickets.get(1);

        assertAll(
            () -> assertEquals(ticketDto1.getId(), ticket1.getId()),
            () -> assertEquals(ticketDto2.getId(), ticket2.getId())
        );
    }

    @Test
    void givenTicket_whenMapToTicketDto_NullFields_thenDtoHasNullFields() {
        // Arrange
        Ticket ticket = new Ticket();
        ticket.setId(null);
        ticket.setShow(null);
        ticket.setStandingSector(null);
        ticket.setSeat(null);
        ticket.setPrice(null);
        ticket.setReserved(null);
        ticket.setPurchased(null);
        ticket.setInCart(null);
        ticket.setDate(null);
        ticket.setUser(null);
        ticket.setOrder(null);
        ticket.setTicketType(null);

        // Act
        TicketDto ticketDto = ticketMapper.ticketToTicketDto(ticket);

        // Assert
        assertNotNull(ticketDto);
        assertAll(
            () -> assertNull(ticketDto.getId()),
            () -> assertNull(ticketDto.getShowId()),
            () -> assertNull(ticketDto.getStandingSectorId()),
            () -> assertNull(ticketDto.getSeatId()),
            () -> assertNull(ticketDto.getPrice()),
            () -> assertNull(ticketDto.getReserved()),
            () -> assertNull(ticketDto.getPurchased()),
            () -> assertNull(ticketDto.getInCart()),
            () -> assertNull(ticketDto.getDate()),
            () -> assertNull(ticketDto.getUserId()),
            () -> assertNull(ticketDto.getOrderId()),
            () -> assertNull(ticketDto.getTicketType())
        );
    }

    @Test
    void givenTicketDto_whenMapToTicket_NullFields_thenEntityHasNullFields() {
        // Arrange
        TicketDto ticketDto = new TicketDto();
        ticketDto.setId(null);
        ticketDto.setShowId(null);
        ticketDto.setStandingSectorId(null);
        ticketDto.setSeatId(null);
        ticketDto.setPrice(null);
        ticketDto.setReserved(null);
        ticketDto.setPurchased(null);
        ticketDto.setInCart(null);
        ticketDto.setDate(null);
        ticketDto.setUserId(null);
        ticketDto.setOrderId(null);
        ticketDto.setTicketType(null);

        // Act
        Ticket ticket = ticketMapper.ticketDtoToTicket(ticketDto);

        // Assert
        assertNotNull(ticket);
        assertAll(
            () -> assertNull(ticket.getId()),
            () -> assertNull(ticket.getPrice()),
            () -> assertNull(ticket.getReserved()),
            () -> assertNull(ticket.getPurchased()),
            () -> assertNull(ticket.getInCart()),
            () -> assertNull(ticket.getDate()),
            () -> assertNull(ticket.getTicketType())
        );
    }
}
