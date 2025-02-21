package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.entity.Order;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;

import java.util.List;

public interface OrderService {
    /**
     * Purchase an order with the given details.
     *
     * @param order the order to purchase
     * @return the purchased order
     * @throws ConflictException if the tickets in the order are not available
     * @throws NotFoundException if the user is not found
     */
    Order purchaseOrder(Order order) throws ConflictException, NotFoundException;

    /**
     * Cancel the purchase of tickets with the given IDs.
     *
     * @param ticketIds the IDs of the tickets to cancel
     * @param userId the ID of the user who purchased the tickets
     * @return the canceled order
     * @throws ConflictException if the tickets are not purchased
     * @throws NotFoundException if the user is not found
     */
    Order cancelPurchase(List<Long> ticketIds, Long userId) throws ConflictException, NotFoundException;

    /**
     * Get all orders for a specific user.
     *
     * @param userId the ID of the user whose orders to retrieve
     * @return list of orders belonging to the user
     * @throws NotFoundException if the user does not exist
     */
    List<Order> getOrdersByUser(Long userId) throws NotFoundException;


    /**
     * Retrieves the PDF file for a cancellation invoice.
     *
     * @param invoiceId the ID of the cancellation invoice
     * @param userId the ID of the user requesting the PDF
     * @return byte array containing the PDF data
     * @throws NotFoundException if the invoice is not found
     * @throws ConflictException if the user is not authorized to access the invoice
     */
    byte[] getCancellationInvoicePdf(Long invoiceId, Long userId) throws NotFoundException, ConflictException;

    /**
     * Get the PDF file for an order invoice.
     *
     * @param orderId the ID of the order
     * @param userId the ID of the user requesting the PDF
     * @return byte array containing the PDF data
     * @throws NotFoundException if the order is not found
     * @throws ConflictException if the user is not authorized to access the invoice
     */
    byte[] getOrderPdf(Long orderId, Long userId) throws NotFoundException, ConflictException;

    /**
     * Get all cancelled orders for a specific user.
     *
     * @param userId the ID of the user whose cancelled orders to retrieve
     * @return list of cancelled orders belonging to the user
     * @throws NotFoundException if the user does not exist
     */
    List<Order> getCancelledOrdersByUser(Long userId) throws NotFoundException;
}