package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TicketDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.TicketMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.FatalException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(value = "/api/v1/tickets")
public class TicketEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final TicketService ticketService;
    private final TicketMapper ticketMapper;

    @Autowired
    public TicketEndpoint(TicketService ticketService, TicketMapper ticketMapper) {
        this.ticketService = ticketService;
        this.ticketMapper = ticketMapper;
    }

    @Secured("ROLE_USER")
    @GetMapping
    public ResponseEntity<List<Ticket>> getTickets(@AuthenticationPrincipal UserDetails userDetails) {
        LOGGER.info("get tickets endpoint.");
        ApplicationUser user = (ApplicationUser) userDetails;
        List<Ticket> tickets = ticketService.getTicketsByUser(user);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(tickets);
    }


    @Secured("ROLE_USER")
    @PostMapping("/addToCart")
    @Operation(summary = "Add tickets to cart", security = @SecurityRequirement(name = "apiKey"))
    @ResponseStatus(HttpStatus.OK)
    public List<TicketDto> addToCart(@RequestBody Map<String, Object> request) throws ConflictException {
        LOGGER.info("POST /api/v1/tickets/addToCart body: {}", request);
        List<Long> ticketIds = ((List<?>) request.get("ticketIds")).stream()
            .map(id -> ((Number) id).longValue())
            .collect(Collectors.toList());
        Long userId = ((Number) request.get("userId")).longValue();

        List<Ticket> tickets = ticketService.addToCart(ticketIds, userId);
        return ticketMapper.ticketToTicketDto(tickets);
    }

    @Secured("ROLE_USER")
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get reserved tickets for a user", security = @SecurityRequirement(name = "apiKey"))
    public List<TicketDto> getUserTickets(@PathVariable(name = "userId") Long userId) throws NotFoundException {
        LOGGER.info("GET /api/v1/tickets/user/{}", userId);

        List<Ticket> tickets = ticketService.getUserTickets(userId);
        List<TicketDto> ret = ticketMapper.ticketToTicketDto(tickets);
        LOGGER.info("Found {} tickets for user {}", ret.size(), userId);
        ret.forEach(ticket -> LOGGER.info("Ticket: id={}, showId={}, seatId={}, standingSectorId={}, price={}, reserved={}, purchased={}, inCart={}",
            ticket.getId(),
            ticket.getShowId(),
            ticket.getSeatId(),
            ticket.getStandingSectorId(),
            ticket.getPrice(),
            ticket.getReserved(),
            ticket.getPurchased(),
            ticket.getInCart()));
        return ret;
    }

    @Secured("ROLE_USER")
    @PostMapping("/cancelReservation")
    @Operation(summary = "Cancel reserved ticket", security = @SecurityRequirement(name = "apiKey"))
    @ResponseStatus(HttpStatus.OK)
    public void cancelTicketReservation(@RequestBody Map<String, Object> request) throws ConflictException, NotFoundException {
        LOGGER.info("POST /api/v1/tickets/cancelReservation body: {}", request);
        Long ticketId = ((Number) request.get("ticketId")).longValue();
        Long userId = ((Number) request.get("userId")).longValue();

        ticketService.cancelTicketReservation(ticketId, userId);
    }

    @Secured("ROLE_USER")
    @DeleteMapping("/cart/{ticketId}")
    @Operation(summary = "Remove ticket from cart", security = @SecurityRequirement(name = "apiKey"))
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFromCart(@PathVariable Long ticketId) throws NotFoundException {
        LOGGER.info("DELETE /api/v1/tickets/cart/{}", ticketId);
        ticketService.removeFromCart(ticketId);
    }

    @Secured("ROLE_USER")
    @PostMapping("/create")
    @Operation(summary = "Create multiple tickets", security = @SecurityRequirement(name = "apiKey"))
    @ResponseStatus(HttpStatus.CREATED)
    public List<TicketDto> createTickets(@Valid @RequestBody List<TicketDto> tickets) throws ConflictException {
        LOGGER.info("POST /api/v1/tickets/create body: {}", tickets);
        List<Ticket> createdTickets = ticketService.createTickets(ticketMapper.ticketDtoToTicket(tickets));
        return ticketMapper.ticketToTicketDto(createdTickets);
    }

    @Secured("ROLE_USER")
    @GetMapping("/{ticketId}/pdf")
    @Operation(summary = "Generate PDF for a ticket", security = @SecurityRequirement(name = "apiKey"))
    public void generateTicketPdf(
        @PathVariable Long ticketId,
        @RequestParam Long userId,
        HttpServletResponse response) throws NotFoundException, ConflictException {
        LOGGER.info("GET /api/v1/tickets/{}/pdf", ticketId);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"ticket-" + ticketId + ".pdf\"");

        try {
            ticketService.generatePdfForTicket(ticketId, userId, response.getOutputStream());
        } catch (IOException e) {
            throw new FatalException("Failed to write PDF to response", e);
        }
    }
}
