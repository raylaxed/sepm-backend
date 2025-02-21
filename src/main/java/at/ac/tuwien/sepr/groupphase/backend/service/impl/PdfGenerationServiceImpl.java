package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.CancellationInvoice;
import at.ac.tuwien.sepr.groupphase.backend.entity.Order;
import at.ac.tuwien.sepr.groupphase.backend.entity.Show;
import at.ac.tuwien.sepr.groupphase.backend.service.PdfGenerationService;
import com.itextpdf.barcodes.BarcodeQRCode;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Table;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import at.ac.tuwien.sepr.groupphase.backend.config.CompanyConfig;

import java.io.OutputStream;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Implementation of the PdfGenerationService using iText.
 */
@Service
public class PdfGenerationServiceImpl implements PdfGenerationService {

    @Autowired
    private CompanyConfig companyConfig;

    private static final float TICKET_WIDTH = PageSize.A4.getWidth();
    private static final float TICKET_HEIGHT = 350f; // Increased to accommodate price
    private static final DeviceRgb DARK_BLUE = new DeviceRgb(0, 0, 102);
    private static final DeviceRgb BANNER_BLUE = new DeviceRgb(24, 32, 132); // Rich blue color

    @Override
    public void generateTicketPdf(Ticket ticket, OutputStream outputStream) throws Exception {
        // Basic null checks
        if (ticket == null) {
            throw new IllegalArgumentException("Ticket cannot be null");
        }
        if (outputStream == null) {
            throw new IllegalArgumentException("OutputStream cannot be null");
        }
        if (ticket.getUser() == null) {
            throw new IllegalArgumentException("Ticket must be associated with a user");
        }

        // Generate UUID if not present
        if (ticket.getTicketUuid() == null) {
            ticket.setTicketUuid(UUID.randomUUID());
        }

        try {
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdf = new PdfDocument(writer);
            // Create custom page size for ticket-like dimensions
            PageSize customPageSize = new PageSize(TICKET_WIDTH, TICKET_HEIGHT);
            Document document = new Document(pdf, customPageSize);
            document.setMargins(20, 20, 20, 20);

            // Create main table with two columns (ticket info and QR code)
            Table mainTable = new Table(new float[]{75f, 25f});
            mainTable.setWidth(TICKET_WIDTH - 40); // Account for margins

            // Left column for ticket information
            Cell leftCell = new Cell();
            leftCell.setBorder(null);

            // Create banner instead of logo
            Table bannerTable = new Table(new float[]{1});
            bannerTable.setWidth(TICKET_WIDTH - 45); // Account for margins
            
            Cell bannerCell = new Cell()
                .setBackgroundColor(BANNER_BLUE)
                .setPadding(10)
                .setBorder(null);

            Paragraph titleText = new Paragraph("TICKETLINE")
                .setFontSize(24)
                .setFontColor(ColorConstants.WHITE)
                .setBold()
                .setTextAlignment(TextAlignment.LEFT);

            bannerCell.add(titleText);
            bannerTable.addCell(bannerCell);
            
            // Add banner to left cell with some margin
            leftCell.add(bannerTable.setMarginBottom(15));

            // Create inner table for ticket details
            Table detailsTable = new Table(new float[]{1});
            detailsTable.setWidth(TICKET_WIDTH - 40);
            detailsTable.setMarginBottom(0); // Reduce bottom margin

            // Format dates
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

            // Add ticket details with styling
            addStyledRow(detailsTable, "EVENT", ticket.getShow().getName());
            addStyledRow(detailsTable, "LOCATION", ticket.getShow().getVenue().getName());
            
            String dateTimeStr = String.format("%s at %s",
                ticket.getShow().getDate().format(dateFormatter),
                ticket.getShow().getTime().format(timeFormatter));
            addStyledRow(detailsTable, "DATE AND TIME", dateTimeStr);

            // Add seating information
            if (ticket.getSeat() != null) {
                String seatInfo = String.format("Sector %s, Row %d, Seat %d",
                    ticket.getSeat().getSector().getSectorName(),
                    ticket.getSeat().getRowSeat(),
                    ticket.getSeat().getColumnSeat());
                addStyledRow(detailsTable, "SEAT", seatInfo);
            } else if (ticket.getStandingSector() != null) {
                addStyledRow(detailsTable, "SECTION", "Standing - " + ticket.getStandingSector().getSectorName());
            }

            // Add price information with less bottom padding
            Cell priceCell = new Cell()
                .add(new Paragraph("PRICE")
                    .setFontSize(8)
                    .setFontColor(ColorConstants.GRAY)
                    .setMarginBottom(0))
                .add(new Paragraph(String.format("€%.2f", ticket.getPrice()))
                    .setFontSize(12)
                    .setFontColor(DARK_BLUE)
                    .setMarginTop(0))
                .setBorder(null)
                .setPaddingBottom(5); // Reduced padding

            detailsTable.addCell(priceCell);

            leftCell.add(detailsTable);

            // Right column for QR code with better alignment
            Cell rightCell = new Cell();
            rightCell.setBorder(null);
            rightCell.setVerticalAlignment(VerticalAlignment.MIDDLE);
            rightCell.setTextAlignment(TextAlignment.CENTER);
            rightCell.setPaddingTop(30); // Add padding to move QR code down

            // Generate QR code
            String qrCodeContent = ticket.getTicketUuid().toString();
            BarcodeQRCode qrCode = new BarcodeQRCode(qrCodeContent);
            Image qrCodeImage = new Image(qrCode.createFormXObject(pdf));
            qrCodeImage.setWidth(90)
                .setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER);
            
            // Add ticket number with better styling
            Paragraph ticketNumber = new Paragraph("#" + ticket.getId())
                .setFontSize(12)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(10)
                .setFontColor(DARK_BLUE);

            rightCell.add(qrCodeImage);
            rightCell.add(ticketNumber);

            // Add cells to main table
            mainTable.addCell(leftCell);
            mainTable.addCell(rightCell);

            // Add border to the entire ticket
            mainTable.setBorder(new SolidBorder(ColorConstants.BLACK, 1));
            
            document.add(mainTable);
            document.close();
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to generate PDF", e);
        }
    }

    @Override
    public void generateOrderPdf(Order order, OutputStream outputStream) throws Exception {
        if (order == null || outputStream == null) {
            throw new IllegalArgumentException("Order and OutputStream must not be null");
        }

        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);
        document.setMargins(40, 40, 40, 40);

        // Load company logo
        Image logo = new Image(ImageDataFactory.create(
            new ClassPathResource("static/images/default_image.png").getURL()));
        logo.setWidth(100);
        logo.setHeight(100);

        // Create header table (2 columns: logo/customer info and company info)
        Table headerTable = new Table(new float[]{1, 1});
        headerTable.setWidth(UnitValue.createPercentValue(100));

        // Left column: Logo and customer info
        Cell leftCell = new Cell();
        leftCell.setBorder(null);
        leftCell.add(logo);
        leftCell.add(new Paragraph("\n"));
        leftCell.add(new Paragraph(order.getUser().getFirstName() + " " + order.getUser().getLastName())
            .setFontSize(12));
        leftCell.add(new Paragraph(order.getUser().getEmail())
            .setFontSize(12));
        // Add user address if available
        if (order.getUser().getAddress() != null && !order.getUser().getAddress().isEmpty()) {
            leftCell.add(new Paragraph(order.getUser().getAddress())
                .setFontSize(12));
        }

        // Right column: Company info and order details
        Cell rightCell = new Cell();
        rightCell.setBorder(null);
        rightCell.setTextAlignment(TextAlignment.RIGHT);
        rightCell.add(new Paragraph(companyConfig.getName()).setBold());
        rightCell.add(new Paragraph(companyConfig.getStreet()));
        rightCell.add(new Paragraph(companyConfig.getPostalCode() + " " + companyConfig.getCity()));
        rightCell.add(new Paragraph(companyConfig.getCountry()));
        rightCell.add(new Paragraph("UID: " + companyConfig.getUid()));
        rightCell.add(new Paragraph("\n"));
        rightCell.add(new Paragraph("Order ID: " + order.getId()));
        rightCell.add(new Paragraph("Date: " 
            + order.getOrderDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))));

        headerTable.addCell(leftCell);
        headerTable.addCell(rightCell);
        document.add(headerTable);

        // Add title and introduction
        document.add(new Paragraph("\n"));
        document.add(new Paragraph("Invoice for ticket purchase")
            .setBold()
            .setFontSize(14));
        document.add(new Paragraph("Dear " + order.getUser().getFirstName() + " " + order.getUser().getLastName() 
            + ", thank you for placing your order with Ticketline. We will charge your order on " 
            + order.getOrderDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + " as follows:")
            .setFontSize(12));
        document.add(new Paragraph("\n"));

        // Create items table
        Table itemsTable = new Table(new float[]{1, 4, 1, 2, 2, 1, 2});
        itemsTable.setWidth(UnitValue.createPercentValue(100));

        // Add table headers
        String[] headers = {"Pos", "Description", "Quantity", "Unit Price", "Price Net", "VAT %", "Price Gross"};
        for (String header : headers) {
            itemsTable.addHeaderCell(new Cell()
                .add(new Paragraph(header).setBold())
                .setBackgroundColor(ColorConstants.LIGHT_GRAY));
        }

        // Group standing tickets by show, sector and price
        Map<String, List<Ticket>> standingTicketGroups = order.getTickets().stream()
            .filter(t -> t.getStandingSector() != null)
            .collect(Collectors.groupingBy(t -> 
                t.getShow().getId() + "-" 
                + t.getStandingSector().getId() + "-" 
                + t.getPrice()));

        // Process seated tickets normally and grouped standing tickets
        int position = 1;
        double totalGross = 0;
        double totalVat = 0;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        // First handle seated tickets individually
        for (Ticket ticket : order.getTickets()) {
            if (ticket.getSeat() != null) {
                Show show = ticket.getShow();
                double priceGross = ticket.getPrice();
                double priceNet = priceGross / (1 + companyConfig.getVatRate() / 100);
                double vatAmount = priceGross - priceNet;
                totalGross += priceGross;
                totalVat += vatAmount;

                StringBuilder description = new StringBuilder();
                description.append(String.format("%s at %s on %s", 
                    show.getName(),
                    show.getVenue().getName(),
                    show.getDate().format(dateFormatter)));

                description.append(String.format("\nSector %s, Row %d, Seat %d",
                    ticket.getSeat().getSector().getSectorName(),
                    ticket.getSeat().getRowSeat(),
                    ticket.getSeat().getColumnSeat()));

                itemsTable.addCell(new Cell().add(new Paragraph(String.valueOf(position++))));
                itemsTable.addCell(new Cell().add(new Paragraph(description.toString())));
                itemsTable.addCell(new Cell().add(new Paragraph("1")));
                itemsTable.addCell(new Cell().add(new Paragraph(String.format("€%.2f", priceNet))));
                itemsTable.addCell(new Cell().add(new Paragraph(String.format("€%.2f", priceNet))));
                itemsTable.addCell(new Cell().add(new Paragraph(String.format("%.0f%%", companyConfig.getVatRate()))));
                itemsTable.addCell(new Cell().add(new Paragraph(String.format("€%.2f", priceGross))));
            }
        }

        // Then handle grouped standing tickets
        for (List<Ticket> ticketGroup : standingTicketGroups.values()) {
            if (!ticketGroup.isEmpty()) {
                Ticket sampleTicket = ticketGroup.get(0);
                Show show = sampleTicket.getShow();
                int quantity = ticketGroup.size();
                double priceGrossPerTicket = sampleTicket.getPrice();
                double totalPriceGross = priceGrossPerTicket * quantity;
                double totalPriceNet = totalPriceGross / (1 + companyConfig.getVatRate() / 100);
                double totalVatAmount = totalPriceGross - totalPriceNet;
                totalGross += totalPriceGross;
                totalVat += totalVatAmount;

                
                StringBuilder description = new StringBuilder();
                description.append(String.format("%s at %s on %s", 
                    show.getName(),
                    show.getVenue().getName(),
                    show.getDate().format(dateFormatter)));
                description.append("\nStanding - " + sampleTicket.getStandingSector().getSectorName());

                double unitPriceNet = totalPriceNet / quantity;
                itemsTable.addCell(new Cell().add(new Paragraph(String.valueOf(position++))));
                itemsTable.addCell(new Cell().add(new Paragraph(description.toString())));
                itemsTable.addCell(new Cell().add(new Paragraph(String.valueOf(quantity))));
                itemsTable.addCell(new Cell().add(new Paragraph(String.format("€%.2f", unitPriceNet))));
                itemsTable.addCell(new Cell().add(new Paragraph(String.format("€%.2f", totalPriceNet))));
                itemsTable.addCell(new Cell().add(new Paragraph(String.format("%.0f%%", companyConfig.getVatRate()))));
                itemsTable.addCell(new Cell().add(new Paragraph(String.format("€%.2f", totalPriceGross))));
            }
        }

        document.add(itemsTable);

        // Add totals
        document.add(new Paragraph("\n"));
        document.add(new Paragraph(String.format("Total Invoice Amount: €%.2f", totalGross))
            .setBold()
            .setFontSize(12));
        document.add(new Paragraph(String.format("Included VAT Amount: €%.2f", totalVat))
            .setFontSize(12));

        document.close();
    }

    @Override
    public void generateCancellationOrderPdf(CancellationInvoice cancellationInvoice, OutputStream outputStream) throws Exception {
        if (cancellationInvoice == null || outputStream == null) {
            throw new IllegalArgumentException("CancellationInvoice and OutputStream must not be null");
        }

        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);
        document.setMargins(40, 40, 40, 40);

        // Add cancellation header
        Paragraph cancellationHeader = new Paragraph("Cancellation Invoice")
            .setBold()
            .setFontSize(20)
            .setFontColor(ColorConstants.RED)
            .setTextAlignment(TextAlignment.CENTER);
        document.add(cancellationHeader);
        document.add(new Paragraph("\n"));

        // Load company logo
        Image logo = new Image(ImageDataFactory.create(
            new ClassPathResource("static/images/default_image.png").getURL()));
        logo.setWidth(100);
        logo.setHeight(100);

        // Create header table (2 columns: logo/customer info and company info)
        Table headerTable = new Table(new float[]{1, 1});
        headerTable.setWidth(UnitValue.createPercentValue(100));

        // Get the user from the first ticket (assuming all tickets belong to same user)
        ApplicationUser user = cancellationInvoice.getTickets().get(0).getUser();

        // Left column: Logo and customer info
        Cell leftCell = new Cell();
        leftCell.setBorder(null);
        leftCell.add(logo);
        leftCell.add(new Paragraph("\n"));
        leftCell.add(new Paragraph(user.getFirstName() + " " + user.getLastName())
            .setFontSize(12));
        leftCell.add(new Paragraph(user.getEmail())
            .setFontSize(12));
        if (user.getAddress() != null && !user.getAddress().isEmpty()) {
            leftCell.add(new Paragraph(user.getAddress())
                .setFontSize(12));
        }

        // Right column: Company info and cancellation details
        Cell rightCell = new Cell();
        rightCell.setBorder(null);
        rightCell.setTextAlignment(TextAlignment.RIGHT);
        rightCell.add(new Paragraph(companyConfig.getName()).setBold());
        rightCell.add(new Paragraph(companyConfig.getStreet()));
        rightCell.add(new Paragraph(companyConfig.getPostalCode() + " " + companyConfig.getCity()));
        rightCell.add(new Paragraph(companyConfig.getCountry()));
        rightCell.add(new Paragraph("UID: " + companyConfig.getUid()));
        rightCell.add(new Paragraph("\n"));
        rightCell.add(new Paragraph("Cancellation Date: " 
            + cancellationInvoice.getCancellationDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))));
        // Get original order date from first ticket
        rightCell.add(new Paragraph("Original Order Date: " 
            + cancellationInvoice.getTickets().get(0).getOrder().getOrderDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))));

        headerTable.addCell(leftCell);
        headerTable.addCell(rightCell);
        document.add(headerTable);

        // Add title and introduction
        document.add(new Paragraph("\n"));
        document.add(new Paragraph("Cancellation Invoice for ticket purchase")
            .setBold()
            .setFontSize(14));
        document.add(new Paragraph("Dear " + user.getFirstName() + " " + user.getLastName() 
            + ", this document confirms the cancellation of your tickets and the refund of your payment.")
            .setFontSize(12));
        document.add(new Paragraph("\n"));

        // Create items table
        Table itemsTable = new Table(new float[]{1, 4, 1, 2, 2, 1, 2});
        itemsTable.setWidth(UnitValue.createPercentValue(100));

        // Add table headers
        String[] headers = {"Pos", "Description", "Quantity", "Unit Price", "Price Net", "VAT %", "Price Gross"};
        for (String header : headers) {
            itemsTable.addHeaderCell(new Cell()
                .add(new Paragraph(header).setBold())
                .setBackgroundColor(ColorConstants.LIGHT_GRAY));
        }

        // Group standing tickets by show, sector and price
        Map<String, List<Ticket>> standingTicketGroups = cancellationInvoice.getTickets().stream()
            .filter(t -> t.getStandingSector() != null)
            .collect(Collectors.groupingBy(t -> 
                t.getShow().getId() + "-" 
                + t.getStandingSector().getId() + "-" 
                + t.getPrice()));

        // Process seated tickets and grouped standing tickets
        int position = 1;
        double totalGross = 0;
        double totalVat = 0;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        // First handle seated tickets individually
        for (Ticket ticket : cancellationInvoice.getTickets()) {
            if (ticket.getSeat() != null) {
                Show show = ticket.getShow();
                double priceGross = ticket.getPrice();
                double priceNet = priceGross / (1 + companyConfig.getVatRate() / 100);
                double vatAmount = priceGross - priceNet;
                totalGross += priceGross;
                totalVat += vatAmount;

                StringBuilder description = new StringBuilder();
                description.append(String.format("%s at %s on %s", 
                    show.getName(),
                    show.getVenue().getName(),
                    show.getDate().format(dateFormatter)));

                description.append(String.format("\nSector %s, Row %d, Seat %d",
                    ticket.getSeat().getSector().getSectorName(),
                    ticket.getSeat().getRowSeat(),
                    ticket.getSeat().getColumnSeat()));

                itemsTable.addCell(new Cell().add(new Paragraph(String.valueOf(position++))));
                itemsTable.addCell(new Cell().add(new Paragraph(description.toString())));
                itemsTable.addCell(new Cell().add(new Paragraph("1")));
                itemsTable.addCell(new Cell().add(new Paragraph(String.format("€%.2f", priceNet))));
                itemsTable.addCell(new Cell().add(new Paragraph(String.format("€%.2f", priceNet))));
                itemsTable.addCell(new Cell().add(new Paragraph(String.format("%.0f%%", companyConfig.getVatRate()))));
                itemsTable.addCell(new Cell().add(new Paragraph(String.format("€%.2f", priceGross))));
            }
        }

        // Then handle grouped standing tickets
        for (List<Ticket> ticketGroup : standingTicketGroups.values()) {
            if (!ticketGroup.isEmpty()) {
                Ticket sampleTicket = ticketGroup.get(0);
                Show show = sampleTicket.getShow();
                int quantity = ticketGroup.size();
                double priceGrossPerTicket = sampleTicket.getPrice();
                double totalPriceGross = priceGrossPerTicket * quantity;
                double totalPriceNet = totalPriceGross / (1 + companyConfig.getVatRate() / 100);
                double totalVatAmount = totalPriceGross - totalPriceNet;
                totalGross += totalPriceGross;
                totalVat += totalVatAmount;

                
                StringBuilder description = new StringBuilder();
                description.append(String.format("%s at %s on %s", 
                    show.getName(),
                    show.getVenue().getName(),
                    show.getDate().format(dateFormatter)));
                description.append("\nStanding - " + sampleTicket.getStandingSector().getSectorName());

                double unitPriceNet = totalPriceNet / quantity;
                itemsTable.addCell(new Cell().add(new Paragraph(String.valueOf(position++))));
                itemsTable.addCell(new Cell().add(new Paragraph(description.toString())));
                itemsTable.addCell(new Cell().add(new Paragraph(String.valueOf(quantity))));
                itemsTable.addCell(new Cell().add(new Paragraph(String.format("€%.2f", unitPriceNet))));
                itemsTable.addCell(new Cell().add(new Paragraph(String.format("€%.2f", totalPriceNet))));
                itemsTable.addCell(new Cell().add(new Paragraph(String.format("%.0f%%", companyConfig.getVatRate()))));
                itemsTable.addCell(new Cell().add(new Paragraph(String.format("€%.2f", totalPriceGross))));
            }
        }

        document.add(itemsTable);

        // Add totals with refund notice
        document.add(new Paragraph("\n"));
        Paragraph totalAmount = new Paragraph(String.format("Total Refunded Amount: €%.2f", totalGross))
            .setBold()
            .setFontSize(12);
        document.add(totalAmount);
        document.add(new Paragraph(String.format("Included VAT Amount: €%.2f", totalVat))
            .setFontSize(12));
        document.add(new Paragraph("Full amount refunded")
            .setFontColor(ColorConstants.RED)
            .setFontSize(12));

        document.close();
    }

    private void addStyledRow(Table table, String label, String value) {
        Paragraph labelParagraph = new Paragraph(label)
            .setFontSize(8)
            .setFontColor(ColorConstants.GRAY)
            .setMarginBottom(0);

        Paragraph valueParagraph = new Paragraph(value)
            .setFontSize(12)
            .setFontColor(DARK_BLUE)
            .setMarginTop(0);

        Cell cell = new Cell()
            .add(labelParagraph)
            .add(valueParagraph)
            .setBorder(null)
            .setPaddingBottom(10);

        table.addCell(cell);
    }
} 