package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.time.LocalDateTime;

public class TicketDto {
    private Long id;
    private Long showId;
    private Long standingSectorId;  // null for seats
    private Long seatId;  // null for standing sectors
    private Double price;
    private Boolean reserved;
    private LocalDateTime date;
    private Boolean purchased;
    private Boolean inCart;
    private Long userId;
    private Long orderId;
    private String ticketType;

    // Default constructor
    public TicketDto() {
    }

    // Getters
    public Long getId() {
        return id;
    }

    public Long getShowId() {
        return showId;
    }

    public Long getStandingSectorId() {
        return standingSectorId;
    }

    public Long getSeatId() {
        return seatId;
    }

    public Double getPrice() {
        return price;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public Boolean getReserved() {
        return reserved;
    }

    public Boolean getPurchased() {
        return purchased;
    }

    public Boolean getInCart() {
        return inCart;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getTicketType() {
        return ticketType;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setShowId(Long showId) {
        this.showId = showId;
    }

    public void setStandingSectorId(Long standingSectorId) {
        this.standingSectorId = standingSectorId;
    }

    public void setSeatId(Long seatId) {
        this.seatId = seatId;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setReserved(Boolean reserved) {
        this.reserved = reserved;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setPurchased(Boolean purchased) {
        this.purchased = purchased;
    }

    public void setInCart(Boolean inCart) {
        this.inCart = inCart;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }

    // Builder pattern
    public static class TicketDtoBuilder {
        private final TicketDto ticketDto;

        public TicketDtoBuilder() {
            ticketDto = new TicketDto();
        }

        public TicketDtoBuilder withId(Long id) {
            ticketDto.setId(id);
            return this;
        }

        public TicketDtoBuilder withShowId(Long showId) {
            ticketDto.setShowId(showId);
            return this;
        }

        public TicketDtoBuilder withStandingSectorId(Long standingSectorId) {
            ticketDto.setStandingSectorId(standingSectorId);
            return this;
        }

        public TicketDtoBuilder withSeatId(Long seatId) {
            ticketDto.setSeatId(seatId);
            return this;
        }

        public TicketDtoBuilder withPrice(Double price) {
            ticketDto.setPrice(price);
            return this;
        }

        public TicketDtoBuilder withReserved(Boolean reserved) {
            ticketDto.setReserved(reserved);
            return this;
        }

        public TicketDtoBuilder withDate(LocalDateTime date) {
            ticketDto.setDate(date);
            return this;
        }

        public TicketDtoBuilder withPurchased(Boolean purchased) {
            ticketDto.setPurchased(purchased);
            return this;
        }

        public TicketDtoBuilder withInCart(Boolean inCart) {
            ticketDto.setInCart(inCart);
            return this;
        }

        public TicketDtoBuilder withUserId(Long userId) {
            ticketDto.setUserId(userId);
            return this;
        }

        public TicketDtoBuilder withOrderId(Long orderId) {
            ticketDto.setOrderId(orderId);
            return this;
        }

        public TicketDtoBuilder withTicketType(String ticketType) {
            ticketDto.setTicketType(ticketType);
            return this;
        }

        public TicketDto build() {
            return ticketDto;
        }
    }
}