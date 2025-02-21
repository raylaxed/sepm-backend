package at.ac.tuwien.sepr.groupphase.backend.service;

import java.util.List;
import java.io.OutputStream;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Order;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;

public interface TicketService {

    /**
     * Creates tickets.
     *
     * @param tickets the tickets to create
     * @return the created tickets
     */
    List<Ticket> createTickets(List<Ticket> tickets) throws ConflictException;

    /**
     * Gets all Tickets associated to a certain user.
     *
     * @param user the user the ticket is associated with
     * @return a list of all the tickets associated with the user
     */
    List<Ticket> getTicketsByUser(ApplicationUser user);

    /**
     * Get all tickets reserved by a specific user.
     *
     * @param userId the ID of the user
     * @return List of tickets reserved by the user
     * @throws NotFoundException if the user is not found
     */
    List<Ticket> getUserTickets(Long userId) throws NotFoundException;

    /**
     * Adds tickets to the cart for a specific user.
     *
     * @param ticketIds the IDs of the tickets to add to the cart
     * @param userId the ID of the user adding the tickets to the cart
     * @return the updated list of tickets
     */
    List<Ticket> addToCart(List<Long> ticketIds, Long userId) throws ConflictException;

    /**
     * Purchases tickets with the given IDs.
     *
     * @param ticketIds the IDs of the tickets to purchase
     * @param userId the ID of the user purchasing the tickets
     * @return the purchased ticket
     */
    List<Ticket> purchaseTickets(List<Long> ticketIds, Long userId) throws ConflictException;

    /**
     * Removes a ticket from the cart.
     *
     * @param ticketId the ID of the ticket to remove from cart
     * @throws NotFoundException if the ticket is not found
     */
    void removeFromCart(Long ticketId) throws NotFoundException;

    List<Ticket> getReservedTickets(Long userId) throws NotFoundException;

    /**
     * Cancels reservations for the given ticket IDs.
     *
     * @param ticketId the IDs of the tickets to cancel reservation
     * @param userId the ID of the user canceling the reservation
     */
    void cancelTicketReservation(Long ticketId, Long userId) throws ConflictException;

    /**
     * Cancels the purchase of tickets with the given IDs.
     *
     * @param ticketIds the IDs of the tickets to cancel
     * @param userId the ID of the user who purchased the tickets
     * @return the order containing the canceled tickets
     * @throws ConflictException if the tickets are not purchased
     * @throws NotFoundException if the user is not found
     */
    Order cancelPurchasedTickets(List<Long> ticketIds, Long userId) throws ConflictException, NotFoundException;

    /**
     * Generates a PDF for a ticket.
     *
     * @param ticketId the ID of the ticket
     * @param userId the ID of the user
     * @param outputStream the output stream to write the PDF to
     * @throws NotFoundException if the ticket is not found
     * @throws ConflictException if there is a conflict
     */
    void generatePdfForTicket(Long ticketId, Long userId, OutputStream outputStream) throws NotFoundException, ConflictException;

    /**
     * Get all tickets associated with an order.
     *
     * @param orderId the ID of the order
     * @return list of tickets belonging to the order
     */
    List<Ticket> getTicketsByOrder(Long orderId);
}