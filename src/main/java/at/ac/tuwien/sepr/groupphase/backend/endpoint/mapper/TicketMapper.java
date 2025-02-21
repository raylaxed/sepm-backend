package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TicketDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ticket;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper interface for converting between Ticket entities and DTOs.
 * This mapper provides methods to convert Ticket entities to DTOs and vice versa,
 * handling the relationships between tickets, shows, sectors, seats, users, and orders.
 */
@Mapper(componentModel = "spring")
public interface TicketMapper {

    /**
     * Converts a Ticket entity to a TicketDto.
     * Maps the IDs from the associated entities including show, standing sector,
     * seat, user, and order.
     *
     * @param ticket the Ticket entity to convert
     * @return the corresponding TicketDto
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "showId", source = "show.id")
    @Mapping(target = "standingSectorId", source = "standingSector.id")
    @Mapping(target = "seatId", source = "seat.seatId")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "reserved", source = "reserved")
    @Mapping(target = "date", source = "date")
    @Mapping(target = "inCart", source = "inCart")
    @Mapping(target = "purchased", source = "purchased")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "orderId", source = "order.id")
    @Mapping(target = "ticketType", source = "ticketType")
    TicketDto ticketToTicketDto(Ticket ticket);

    /**
     * Converts a list of Ticket entities to a list of TicketDtos.
     *
     * @param tickets the list of Ticket entities to convert
     * @return the corresponding list of TicketDtos
     */
    List<TicketDto> ticketToTicketDto(List<Ticket> tickets);

    /**
     * Converts a TicketDto to a Ticket entity.
     * Creates the relationships with show, standing sector, seat, user, and order
     * using their respective IDs.
     *
     * @param ticketDto the TicketDto to convert
     * @return the corresponding Ticket entity
     */
    @Mapping(target = "show.id", source = "showId")
    @Mapping(target = "standingSector.id", source = "standingSectorId")
    @Mapping(target = "seat.seatId", source = "seatId")
    @Mapping(target = "user.id", source = "userId")
    @Mapping(target = "order.id", source = "orderId")
    @Mapping(target = "ticketType", source = "ticketType")
    @Mapping(target = "date", source = "date")
    Ticket ticketDtoToTicket(TicketDto ticketDto);

    /**
     * Converts a list of TicketDtos to a list of Ticket entities.
     *
     * @param ticketDtos the list of TicketDtos to convert
     * @return the corresponding list of Ticket entities
     */
    List<Ticket> ticketDtoToTicket(List<TicketDto> ticketDtos);
}