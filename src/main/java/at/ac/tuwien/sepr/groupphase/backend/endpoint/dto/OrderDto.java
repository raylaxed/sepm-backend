package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.time.LocalDateTime;
import java.util.List;

public class OrderDto {
    private Long id;
    private Double total;
    private LocalDateTime orderDate;
    private String paymentIntentId;
    private List<TicketDto> tickets;
    private Long userId;
    private Boolean cancelled;
    private Long cancellationInvoiceId;

    // Default constructor
    public OrderDto() {
    }

    // Getters
    public Long getId() {
        return id;
    }

    public Double getTotal() {
        return total;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public List<TicketDto> getTickets() {
        return tickets;
    }

    public Long getUserId() {
        return userId;
    }

    public String getPaymentIntentId() {
        return paymentIntentId;
    }

    public Boolean getCancelled() {
        return cancelled;
    }

    public Long getCancellationInvoiceId() {
        return cancellationInvoiceId;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public void setTickets(List<TicketDto> tickets) {
        this.tickets = tickets;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setPaymentIntentId(String paymentIntentId) {
        this.paymentIntentId = paymentIntentId;
    }

    public void setCancelled(Boolean cancelled) {
        this.cancelled = cancelled;
    }

    public void setCancellationInvoiceId(Long cancellationInvoiceId) {
        this.cancellationInvoiceId = cancellationInvoiceId;
    }

    // Builder pattern
    public static class OrderDtoBuilder {
        private Long id;
        private Double total;
        private LocalDateTime orderDate;
        private String paymentIntentId;
        private List<TicketDto> tickets;
        private Long userId;
        private Boolean cancelled;
        private Long cancellationInvoiceId;

        public static OrderDtoBuilder anOrderDto() {
            return new OrderDtoBuilder();
        }


        public OrderDtoBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public OrderDtoBuilder withTotal(Double total) {
            this.total = total;
            return this;
        }

        public OrderDtoBuilder withOrderDate(LocalDateTime orderDate) {
            this.orderDate = orderDate;
            return this;
        }

        public OrderDtoBuilder withTickets(List<TicketDto> tickets) {
            this.tickets = tickets;
            return this;
        }

        public OrderDtoBuilder withUserId(Long userId) {
            this.userId = userId;
            return this;
        }

        public OrderDtoBuilder withPaymentIntentId(String paymentIntentId) {
            this.paymentIntentId = paymentIntentId;
            return this;
        }

        public OrderDtoBuilder withCancelled(Boolean cancelled) {
            this.cancelled = cancelled;
            return this;
        }

        public OrderDtoBuilder withCancellationInvoiceId(Long cancellationInvoiceId) {
            this.cancellationInvoiceId = cancellationInvoiceId;
            return this;
        }

        public OrderDto build() {
            OrderDto orderDto = new OrderDto();
            orderDto.setId(id);
            orderDto.setTotal(total);
            orderDto.setOrderDate(orderDate);
            orderDto.setPaymentIntentId(paymentIntentId);
            orderDto.setTickets(tickets);
            orderDto.setUserId(userId);
            orderDto.setCancelled(cancelled);
            orderDto.setCancellationInvoiceId(cancellationInvoiceId);
            return orderDto;
        }
    }
}