package at.ac.tuwien.sepr.groupphase.backend.unittests;

import at.ac.tuwien.sepr.groupphase.backend.entity.Order;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepr.groupphase.backend.entity.CancellationInvoice;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.CancellationInvoiceRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.OrderRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.TicketService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.SimpleOrderService;
import at.ac.tuwien.sepr.groupphase.backend.service.StripeService;
import at.ac.tuwien.sepr.groupphase.backend.service.PdfGenerationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class OrderServiceTest {

    @Autowired
    private SimpleOrderService orderService;

    @MockBean
    private OrderRepository orderRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private TicketService ticketService;

    @MockBean
    private StripeService stripeService;

    @MockBean
    private CancellationInvoiceRepository cancellationInvoiceRepository;

    @MockBean
    private PdfGenerationService pdfGenerationService;

    private Order order;
    private Ticket ticket;

    @BeforeEach
    void setUp() {
        ticket = new Ticket();
        ticket.setId(1L);
        ticket.setPurchased(true);
        ticket.setPrice(100.0);

        order = new Order();
        order.setId(1L);
        order.setTickets(List.of(ticket));
        order.setPaymentIntentId("pi_123456789");
        ticket.setOrder(order);

        // Set up temporary directory for PDF storage
        String tempDir = System.getProperty("java.io.tmpdir");
        ReflectionTestUtils.setField(orderService, "cancellationInvoicesDir", tempDir);
    }

    @Test
    void givenValidTicketIds_whenCancelPurchase_thenSuccess() throws Exception {
        // Arrange
        when(ticketService.cancelPurchasedTickets(List.of(1L), 1L)).thenReturn(order);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        doNothing().when(stripeService).refundPayment(anyString(), anyDouble());
        doNothing().when(pdfGenerationService).generateCancellationOrderPdf(any(), any());

        // Capture the CancellationInvoice that gets saved
        ArgumentCaptor<CancellationInvoice> invoiceCaptor = ArgumentCaptor.forClass(CancellationInvoice.class);
        when(cancellationInvoiceRepository.save(invoiceCaptor.capture())).thenAnswer(i -> {
            CancellationInvoice invoice = invoiceCaptor.getValue();
            invoice.setId(1L);
            return invoice;
        });

        // Act
        Order result = orderService.cancelPurchase(List.of(1L), 1L);

        // Assert
        assertNotNull(result);
        assertEquals(order.getId(), result.getId());
        
        // Verify all the service calls
        verify(ticketService, times(1)).cancelPurchasedTickets(List.of(1L), 1L);
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(stripeService, times(1)).refundPayment(anyString(), anyDouble());
        
        // Verify PDF generation
        verify(pdfGenerationService, times(1)).generateCancellationOrderPdf(any(), any());
        
        // Verify cancellation invoice was saved
        verify(cancellationInvoiceRepository, times(1)).save(any());
        
        // Verify the saved cancellation invoice properties
        CancellationInvoice savedInvoice = invoiceCaptor.getValue();
        assertNotNull(savedInvoice);
        assertEquals(1L, savedInvoice.getUserId());
        assertEquals(100.0, savedInvoice.getTotalPrice());
        assertNotNull(savedInvoice.getCancellationDate());
        assertNotNull(savedInvoice.getPdfPath());
        assertTrue(savedInvoice.getTickets().contains(ticket));
    }

    @Test
    void givenInvalidTicketIds_whenCancelPurchase_thenThrowNotFoundException() throws ConflictException {
        // Arrange
        when(ticketService.cancelPurchasedTickets(List.of(1L), 1L)).thenThrow(new NotFoundException("Ticket not found"));

        // Act & Assert
        assertThrows(NotFoundException.class, () -> orderService.cancelPurchase(List.of(1L), 1L));
    }
}