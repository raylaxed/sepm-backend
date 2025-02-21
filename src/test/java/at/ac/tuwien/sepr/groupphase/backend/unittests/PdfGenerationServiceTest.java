package at.ac.tuwien.sepr.groupphase.backend.unittests;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepr.groupphase.backend.service.PdfGenerationService;
import at.ac.tuwien.sepr.groupphase.backend.entity.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class PdfGenerationServiceTest implements TestData {

    @Autowired
    private PdfGenerationService pdfGenerationService;

    @Test
    void generateTicketPdf_withValidTicket_shouldGeneratePdfSuccessfully() throws Exception {
        // Arrange
        Ticket ticket = TestData.createTestTicket();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Act
        pdfGenerationService.generateTicketPdf(ticket, outputStream);

        // Assert
        byte[] pdfContent = outputStream.toByteArray();
        assertTrue(pdfContent.length > 0, "PDF should not be empty");
        assertValidPdfHeader(pdfContent);
    }

    @Test
    void generateTicketPdf_withNullTicket_shouldThrowException() {
        // Arrange
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
            () -> pdfGenerationService.generateTicketPdf(null, outputStream));
    }

    @Test
    void generateTicketPdf_withNullOutputStream_shouldThrowException() {
        // Arrange
        Ticket ticket = TestData.createTestTicket();

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
            () -> pdfGenerationService.generateTicketPdf(ticket, null));
    }

    @Test
    void generateOrderPdf_withValidOrder_shouldGeneratePdfSuccessfully() throws Exception {
        // Arrange
        Order order = createTestOrder();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Act
        pdfGenerationService.generateOrderPdf(order, outputStream);

        // Assert
        byte[] pdfContent = outputStream.toByteArray();
        assertTrue(pdfContent.length > 0, "PDF should not be empty");
        assertValidPdfHeader(pdfContent);
    }

    @Test
    void generateOrderPdf_withNullOrder_shouldThrowException() {
        // Arrange
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
            () -> pdfGenerationService.generateOrderPdf(null, outputStream));
    }

    @Test
    void generateOrderPdf_withNullOutputStream_shouldThrowException() {
        // Arrange
        Order order = createTestOrder();

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
            () -> pdfGenerationService.generateOrderPdf(order, null));
    }

    private void assertValidPdfHeader(byte[] pdfContent) {
        // PDF files start with "%PDF-"
        byte[] pdfHeader = new byte[]{0x25, 0x50, 0x44, 0x46, 0x2D};
        byte[] actualHeader = new byte[5];
        System.arraycopy(pdfContent, 0, actualHeader, 0, 5);
        assertArrayEquals(pdfHeader, actualHeader, "PDF header is invalid");
    }

    private Order createTestOrder() {
        // Create a test ticket
        Ticket ticket = TestData.createTestTicket();

        // Create and return a test order using the Order.OrderBuilder
        return Order.OrderBuilder.anOrder()
            .withId(1L)
            .withTotal(TEST_TICKET_PRICE)
            .withOrderDate(LocalDateTime.now())
            .withTickets(List.of(ticket))
            .withUser(ticket.getUser())
            .build();
    }
}