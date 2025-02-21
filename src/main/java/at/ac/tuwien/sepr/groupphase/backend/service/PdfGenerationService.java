package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepr.groupphase.backend.entity.CancellationInvoice;
import at.ac.tuwien.sepr.groupphase.backend.entity.Order;
import java.io.OutputStream;

/**
 * Service for generating PDF documents.
 */
public interface PdfGenerationService {

    /**
     * Generates a PDF document for a given ticket.
     *
     * @param ticket the ticket for which the PDF is to be generated
     * @param outputStream the output stream to which the PDF will be written
     * @throws Exception if an error occurs during PDF generation
     */
    void generateTicketPdf(Ticket ticket, OutputStream outputStream) throws Exception;

    /**
     * Generates an invoice PDF document for a given order.
     * The invoice includes company details, customer information, and itemized list of tickets
     * with their prices (net, VAT, and gross amounts).
     *
     * @param order the order for which the invoice PDF is to be generated
     * @param outputStream the output stream to which the PDF will be written
     * @throws Exception if an error occurs during PDF generation
     */
    void generateOrderPdf(Order order, OutputStream outputStream) throws Exception;

    /**
     * Generates a cancellation invoice PDF document for a given cancellation invoice.
     * The invoice includes company details, customer information, and itemized list of cancelled tickets
     * with their refunded prices.
     *
     * @param cancellationInvoice the cancellation invoice for which the PDF is to be generated
     * @param outputStream the output stream to which the PDF will be written
     * @throws Exception if an error occurs during PDF generation
     */
    void generateCancellationOrderPdf(CancellationInvoice cancellationInvoice, OutputStream outputStream) throws Exception;
} 