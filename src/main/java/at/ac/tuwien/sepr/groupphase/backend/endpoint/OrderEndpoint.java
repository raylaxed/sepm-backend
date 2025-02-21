package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OrderDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TicketDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.OrderMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Order;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.OrderService;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import at.ac.tuwien.sepr.groupphase.backend.exception.FatalException;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1/orders")
public class OrderEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final OrderService orderService;
    private final OrderMapper orderMapper;

    public OrderEndpoint(OrderService orderService, OrderMapper orderMapper) {
        this.orderService = orderService;
        this.orderMapper = orderMapper;
    }

    @Secured("ROLE_USER")
    @PostMapping("/purchase")
    @Operation(summary = "Purchase order", security = @SecurityRequirement(name = "apiKey"))
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDto purchaseOrder(@RequestBody OrderDto orderDto) throws ConflictException {
        LOGGER.info("POST /api/v1/orders/purchase body: {}", orderDto);
        return orderMapper.orderToOrderDto(
            orderService.purchaseOrder(orderMapper.orderDtoToOrder(orderDto))
        );
    }

    @Secured("ROLE_USER")
    @PostMapping("/cancelPurchase")
    @Operation(summary = "Cancel purchased tickets", security = @SecurityRequirement(name = "apiKey"))
    @ResponseStatus(HttpStatus.OK)
    public OrderDto cancelPurchase(@RequestBody Map<String, Object> request) throws ConflictException, NotFoundException {
        LOGGER.info("POST /api/v1/orders/cancelPurchase body: {}", request);
        List<Long> ticketIds = ((List<?>) request.get("ticketIds")).stream()
            .map(id -> ((Number) id).longValue())
            .collect(Collectors.toList());
        Long userId = ((Number) request.get("userId")).longValue();

        if (ticketIds.isEmpty()) {
            throw new IllegalArgumentException("Invalid request: ticketIds is empty");
        }

        Order order = orderService.cancelPurchase(ticketIds, userId);
        OrderDto orderDto = orderMapper.orderToOrderDto(order);
        
        LOGGER.info("Successfully cancelled order ID: {}, generated cancellation invoice ID: {}", 
            orderDto.getId(), orderDto.getCancellationInvoiceId());
        
        return orderDto;
    }

    @Secured("ROLE_USER")
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all orders for a user", security = @SecurityRequirement(name = "apiKey"))
    public List<OrderDto> getOrders(@PathVariable(name = "userId") Long userId) throws NotFoundException {
        LOGGER.info("GET /api/v1/orders/user/{} - getting orders for user", userId);
        List<Order> orders = orderService.getOrdersByUser(userId);
        List<OrderDto> orderDtos = orders.stream()
            .map(orderMapper::orderToOrderDto)
            .collect(Collectors.toList());

        // Detailed logging of orders and their tickets
        for (OrderDto order : orderDtos) {
            LOGGER.info("Order ID: {}, Total: {}, Date: {}, Number of tickets: {}",
                order.getId(), order.getTotal(), order.getOrderDate(),
                order.getTickets() != null ? order.getTickets().size() : 0);

            if (order.getTickets() != null) {
                for (TicketDto ticket : order.getTickets()) {
                    LOGGER.info("  Ticket ID: {}, Show ID: {}, Price: {}, Type: {}, Seat/Sector: {}",
                        ticket.getId(), ticket.getShowId(), ticket.getPrice(),
                        ticket.getTicketType(),
                        ticket.getSeatId() != null ? "Seat " + ticket.getSeatId() :
                            "Standing Sector " + ticket.getStandingSectorId());
                }
            }
        }

        LOGGER.info("GET /api/v1/orders/user/{} - returning {} orders", userId, orderDtos.size());
        return orderDtos;
    }

    @Secured("ROLE_USER")
    @GetMapping("/{orderId}/pdf")
    @Operation(summary = "Get PDF for an order", security = @SecurityRequirement(name = "apiKey"))
    public void getOrderPdf(
        @PathVariable Long orderId,
        @RequestParam Long userId,
        HttpServletResponse response) throws NotFoundException, ConflictException {
        LOGGER.info("GET /api/v1/orders/{}/pdf", orderId);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"order-" + orderId + ".pdf\"");

        try {
            byte[] pdfBytes = orderService.getOrderPdf(orderId, userId);
            response.getOutputStream().write(pdfBytes);
            response.getOutputStream().flush();
        } catch (IOException e) {
            throw new FatalException("Failed to write PDF to response", e);
        }
    }

    @Secured("ROLE_USER")
    @GetMapping("/cancellation-invoice/{invoiceId}/pdf")
    @Operation(summary = "Get PDF for a cancellation invoice", security = @SecurityRequirement(name = "apiKey"))
    public void getCancellationInvoicePdf(
        @PathVariable Long invoiceId,
        @RequestParam Long userId,
        HttpServletResponse response) throws NotFoundException, ConflictException {
        LOGGER.info("GET /api/v1/orders/cancellation-invoice/{}/pdf", invoiceId);
        
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", 
            "attachment; filename=\"cancellation-invoice-" + invoiceId + ".pdf\"");
        
        try {
            byte[] pdfBytes = orderService.getCancellationInvoicePdf(invoiceId, userId);
            response.getOutputStream().write(pdfBytes);
            response.getOutputStream().flush();
        } catch (IOException e) {
            throw new FatalException("Failed to write PDF to response", e);
        }
    }

    @Secured("ROLE_USER")
    @GetMapping("/user/{userId}/cancelled")
    @Operation(summary = "Get all cancelled orders for a user", security = @SecurityRequirement(name = "apiKey"))
    public List<OrderDto> getCancelledOrders(@PathVariable(name = "userId") Long userId) throws NotFoundException {
        LOGGER.info("GET /api/v1/orders/user/{}/cancelled - getting cancelled orders for user", userId);
        List<Order> orders = orderService.getCancelledOrdersByUser(userId);
        List<OrderDto> orderDtos = orders.stream()
            .map(orderMapper::orderToOrderDto)
            .collect(Collectors.toList());
        
        // Detailed logging of cancelled orders and their tickets
        for (OrderDto order : orderDtos) {
            LOGGER.info("Cancelled Order ID: {}, Total: {}, Date: {}, Cancellation Invoice ID: {}, Number of tickets: {}", 
                order.getId(), order.getTotal(), order.getOrderDate(), 
                order.getCancellationInvoiceId(),
                order.getTickets() != null ? order.getTickets().size() : 0);
            
            if (order.getTickets() != null) {
                for (TicketDto ticket : order.getTickets()) {
                    LOGGER.info("  Ticket ID: {}, Show ID: {}, Price: {}, Type: {}, Seat/Sector: {}", 
                        ticket.getId(), ticket.getShowId(), ticket.getPrice(), 
                        ticket.getTicketType(),
                        ticket.getSeatId() != null ? "Seat " + ticket.getSeatId() : 
                            "Standing Sector " + ticket.getStandingSectorId());
                }
            }
        }
        
        LOGGER.info("GET /api/v1/orders/user/{}/cancelled - returning {} cancelled orders", userId, orderDtos.size());
        return orderDtos;
    }
}