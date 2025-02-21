package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.entity.CancellationInvoice;
import at.ac.tuwien.sepr.groupphase.backend.entity.Order;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.FatalException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.CancellationInvoiceRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.OrderRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.OrderService;
import at.ac.tuwien.sepr.groupphase.backend.service.PdfGenerationService;
import at.ac.tuwien.sepr.groupphase.backend.service.TicketService;
import at.ac.tuwien.sepr.groupphase.backend.service.StripeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import com.stripe.exception.StripeException;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.io.IOException;

@Service
@Transactional
public class SimpleOrderService implements OrderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final TicketService ticketService;
    private final CancellationInvoiceRepository cancellationInvoiceRepository;
    private final StripeService stripeService;
    private final PdfGenerationService pdfGenerationService;

    @Value("${app.storage.image-directory}")
    private String storageBaseDir;

    @Value("${app.storage.cancellation-invoices-directory}")
    private String cancellationInvoicesDir;

    @Value("${app.storage.invoices-directory}")
    private String invoicesDirectory;

    public SimpleOrderService(OrderRepository orderRepository, UserRepository userRepository,
                              TicketService ticketService, CancellationInvoiceRepository cancellationInvoiceRepository,
                              PdfGenerationService pdfGenerationService, StripeService stripeService) {

        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.ticketService = ticketService;
        this.cancellationInvoiceRepository = cancellationInvoiceRepository;
        this.stripeService = stripeService;
        this.pdfGenerationService = pdfGenerationService;
    }

    private Order generateAndSaveInvoicePdf(Order order) throws Exception {
        // Create directory if it doesn't exist
        Files.createDirectories(Paths.get(invoicesDirectory));
        
        // Generate unique filename
        String filename = String.format("invoice_%d_%s.pdf", 
            order.getId(), 
            order.getOrderDate().toString().replace(":", "-"));
        Path pdfPath = Paths.get(invoicesDirectory, filename);
        
        // Generate PDF
        try (FileOutputStream outputStream = new FileOutputStream(pdfPath.toFile())) {
            pdfGenerationService.generateOrderPdf(order, outputStream);
        }
        
        // Set the path in the order
        order.setInvoicePath(pdfPath.toString());
        return order;
    }

    @Override
    public Order purchaseOrder(Order order) throws ConflictException, NotFoundException {
        LOGGER.debug("Purchase order {}", order);

        // Validate user exists
        var user = userRepository.findById(order.getUser().getId())
            .orElseThrow(() -> new NotFoundException("User not found"));
        order.setUser(user);

        // Set order date
        order.setOrderDate(LocalDateTime.now());

        // Purchase tickets and set order reference
        List<Long> ticketIds = order.getTickets().stream()
            .map(Ticket::getId)
            .toList();
        List<Ticket> purchasedTickets = ticketService.purchaseTickets(ticketIds, user.getId());
        purchasedTickets.forEach(ticket -> ticket.setOrder(order));
        order.setTickets(purchasedTickets);

        // Calculate total if not set
        if (order.getTotal() == null) {
            order.setTotal(purchasedTickets.stream()
                .mapToDouble(Ticket::getPrice)
                .sum());
        }

        Order savedOrder = orderRepository.save(order);
        
        try {
            savedOrder = generateAndSaveInvoicePdf(savedOrder);
            return orderRepository.save(savedOrder);
        } catch (Exception e) {
            throw new FatalException("Failed to generate invoice PDF", e);
        }
    }

    @Override
    public Order cancelPurchase(List<Long> ticketIds, Long userId) throws ConflictException, NotFoundException {
        LOGGER.debug("Cancel purchase for tickets with ids: {} for user with id: {}", ticketIds, userId);

        if (ticketIds == null || ticketIds.isEmpty() || userId == null) {
            throw new IllegalArgumentException("Invalid input: ticketIds or userId is null or empty");
        }

        // First get the tickets to check their show dates
        List<Ticket> tickets = ticketService.getTicketsByOrder(ticketIds.get(0));

        // Check if any of the tickets are for past shows
        boolean hasPastShow = tickets.stream()
            .filter(ticket -> ticketIds.contains(ticket.getId()))
            .anyMatch(ticket -> {
                LocalDateTime showDateTime = LocalDateTime.of(
                    ticket.getShow().getDate(),
                    ticket.getShow().getTime()
                );
                return LocalDateTime.now().isAfter(showDateTime);
            });

        if (hasPastShow) {
            throw new ConflictException("Cannot cancel tickets for past shows",
                List.of("One or more tickets belong to shows that have already taken place"));
        }

        // Only proceed with cancellation if no past shows were found
        Order order = ticketService.cancelPurchasedTickets(ticketIds, userId);

        // Set the order as cancelled
        order.setCancelled(true);

        // Calculate refund amount
        double refundAmount = order.getTickets().stream()
            .filter(ticket -> ticketIds.contains(ticket.getId()))
            .mapToDouble(Ticket::getPrice)
            .sum();

        try {
            stripeService.refundPayment(order.getPaymentIntentId(), refundAmount);
        } catch (StripeException e) {
            throw new ConflictException("Failed to process refund",
                List.of("Stripe refund processing failed: " + e.getMessage()));
        }

        // Create and save the cancellation invoice
        List<Ticket> canceledTickets = order.getTickets().stream()
            .filter(ticket -> ticketIds.contains(ticket.getId()))
            .collect(Collectors.toList());

        double cancellationTotalPrice = canceledTickets.stream()
            .mapToDouble(Ticket::getPrice)
            .sum();

        CancellationInvoice cancellationInvoice = new CancellationInvoice();
        cancellationInvoice.setUserId(userId);
        cancellationInvoice.setCancellationDate(LocalDateTime.now());
        cancellationInvoice.setTotalPrice(cancellationTotalPrice);
        cancellationInvoice.setTickets(canceledTickets);

        // Generate and save PDF
        try {
            // Create directories if they don't exist
            Path invoicesDir = Paths.get(cancellationInvoicesDir);
            Files.createDirectories(invoicesDir);

            // Generate unique filename
            String filename = String.format("cancellation_invoice_%d_%s.pdf", 
                userId, UUID.randomUUID().toString());
            Path pdfPath = invoicesDir.resolve(filename);

            // Generate PDF
            try (FileOutputStream fos = new FileOutputStream(pdfPath.toFile())) {
                pdfGenerationService.generateCancellationOrderPdf(cancellationInvoice, fos);
            }

            // Set PDF path in entity
            cancellationInvoice.setPdfPath(pdfPath.toString());

        } catch (Exception e) {
            LOGGER.error("Failed to generate or save cancellation PDF", e);
            throw new FatalException("Failed to generate cancellation PDF", e);
        }

        CancellationInvoice savedInvoice = cancellationInvoiceRepository.save(cancellationInvoice);
        
        // Set the cancellation invoice ID in the order
        order.setCancellationInvoiceId(savedInvoice.getId());

        return orderRepository.save(order);
    }

    @Override
    public byte[] getCancellationInvoicePdf(Long invoiceId, Long userId) throws NotFoundException, ConflictException {
        LOGGER.debug("Getting cancellation invoice PDF for invoice {} and user {}", invoiceId, userId);

        CancellationInvoice invoice = cancellationInvoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new NotFoundException("Cancellation invoice not found"));

        // Check if user owns the invoice
        if (!invoice.getUserId().equals(userId)) {
            throw new ConflictException("Access denied", 
                List.of("You are not authorized to access this invoice"));
        }

        try {
            return Files.readAllBytes(Paths.get(invoice.getPdfPath()));
        } catch (Exception e) {
            throw new FatalException("Failed to read cancellation invoice PDF", e);
        }
    }

    @Override
    public List<Order> getOrdersByUser(Long userId) throws NotFoundException {
        LOGGER.debug("Get orders for user with ID {}", userId);

        // Check if user exists
        ApplicationUser user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException(String.format("Could not find user with ID %d", userId)));
        

        List<Order> orders = orderRepository.findByUserAndCancelledFalse(user);

        // For each order, get the associated tickets using ticketService
        for (Order order : orders) {
            List<Ticket> tickets = ticketService.getTicketsByOrder(order.getId());
            order.setTickets(tickets);
        }

        return orders;
    }

    @Override
    public byte[] getOrderPdf(Long orderId, Long userId) throws NotFoundException, ConflictException {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new NotFoundException("Order not found"));
            
        if (!order.getUser().getId().equals(userId)) {
            throw new ConflictException("User not authorized to access this order", 
                List.of("The user does not own this order"));
        }

        try {
            return Files.readAllBytes(Paths.get(order.getInvoicePath()));
        } catch (IOException e) {
            throw new FatalException("Failed to read invoice PDF", e);
        }
    }

    @Override
    public List<Order> getCancelledOrdersByUser(Long userId) throws NotFoundException {
        LOGGER.debug("Get cancelled orders for user with ID {}", userId);
        
        // Check if user exists
        ApplicationUser user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException(String.format("Could not find user with ID %d", userId)));
        
        List<Order> orders = orderRepository.findByUserAndCancelledTrue(user);

        // For each order, get the associated tickets using ticketService
        for (Order order : orders) {
            List<Ticket> tickets = ticketService.getTicketsByOrder(order.getId());
            order.setTickets(tickets);
        }

        return orders;
    }
}