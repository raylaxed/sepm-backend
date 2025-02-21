package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

import java.time.LocalDateTime;
import java.util.Objects;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.UUID;

@Entity
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotNull(message = "Ticket type must not be null")
    private String ticketType;

    @Column(nullable = false)
    @NotNull(message = "Price must not be null")
    @Positive(message = "Price must be positive")
    private Double price;

    @Column(nullable = false)
    @NotNull(message = "Reserved status must not be null")
    private Boolean reserved = false;

    private LocalDateTime date;

    @Column(nullable = false)
    @NotNull(message = "Purchased status must not be null")
    private Boolean purchased = false;

    @Column(nullable = false)
    @NotNull(message = "Cart status must not be null")
    private Boolean inCart = false;

    @ManyToOne
    @JoinColumn(name = "seat_id")
    private Seat seat;

    @ManyToOne
    @JoinColumn(name = "standing_sector_id")
    private StandingSector standingSector;

    @ManyToOne
    @JoinColumn(name = "show_id", nullable = false)
    @NotNull(message = "Show must not be null")
    private Show show;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private ApplicationUser user;

    @Column(unique = true)
    private UUID ticketUuid;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTicketType() {
        return ticketType;
    }

    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Boolean getReserved() {
        return reserved;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setReserved(Boolean reserved) {
        this.reserved = reserved;
    }

    public Boolean getPurchased() {
        return purchased;
    }

    public void setPurchased(Boolean purchased) {
        this.purchased = purchased;
    }

    public Boolean getInCart() {
        return inCart;
    }

    public void setInCart(Boolean inCart) {
        this.inCart = inCart;
    }

    public Seat getSeat() {
        return seat;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }

    public StandingSector getStandingSector() {
        return standingSector;
    }

    public void setStandingSector(StandingSector standingSector) {
        this.standingSector = standingSector;
    }

    public Show getShow() {
        return show;
    }

    public void setShow(Show show) {
        this.show = show;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public ApplicationUser getUser() {
        return user;
    }

    public void setUser(ApplicationUser user) {
        this.user = user;
    }

    public UUID getTicketUuid() {
        return ticketUuid;
    }

    public void setTicketUuid(UUID ticketUuid) {
        this.ticketUuid = ticketUuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Ticket ticket)) {
            return false;
        }
        return Objects.equals(id, ticket.id)
            && Objects.equals(ticketType, ticket.ticketType)
            && Objects.equals(price, ticket.price)
            && Objects.equals(reserved, ticket.reserved)
            && Objects.equals(date, ticket.date)
            && Objects.equals(purchased, ticket.purchased)
            && Objects.equals(inCart, ticket.inCart)
            && Objects.equals(seat, ticket.seat)
            && Objects.equals(standingSector, ticket.standingSector)
            && Objects.equals(show, ticket.show)
            && Objects.equals(order, ticket.order)
            && Objects.equals(user, ticket.user)
            && Objects.equals(ticketUuid, ticket.ticketUuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ticketType, price, reserved, date, purchased, inCart,
            seat, standingSector, show, order, user, ticketUuid);
    }

    @Override
    public String toString() {
        return "Ticket{"
            + "id=" + id
            + ", ticketType='" + ticketType + '\''
            + ", price=" + price
            + ", reserved=" + reserved
            + ", purchased=" + purchased
            + ", inCart=" + inCart
            + ", date=" + date
            + ", seat=" + seat
            + ", standingSector=" + standingSector
            + ", show=" + show
            + ", order=" + order
            + ", user=" + user
            + ", ticketUuid=" + ticketUuid
            + '}';
    }

    // Builder pattern
    public static final class TicketBuilder {
        private Long id;
        private String ticketType;
        private Double price;
        private Boolean reserved = false;
        private Seat seat;
        private StandingSector standingSector;
        private Show show;
        private Order order;
        private ApplicationUser user;
        private Boolean purchased = false;
        private Boolean inCart = false;
        private LocalDateTime date;
        private UUID ticketUuid;

        private TicketBuilder() {
        }

        public static TicketBuilder aTicket() {
            return new TicketBuilder();
        }

        public TicketBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public TicketBuilder withTicketType(String ticketType) {
            this.ticketType = ticketType;
            return this;
        }

        public TicketBuilder withPrice(Double price) {
            this.price = price;
            return this;
        }

        public TicketBuilder withReserved(Boolean reserved) {
            this.reserved = reserved;
            return this;
        }

        public TicketBuilder withDate(LocalDateTime date) {
            this.date = date;
            return this;
        }

        public TicketBuilder withSeat(Seat seat) {
            this.seat = seat;
            return this;
        }

        public TicketBuilder withStandingSector(StandingSector standingSector) {
            this.standingSector = standingSector;
            return this;
        }

        public TicketBuilder withShow(Show show) {
            this.show = show;
            return this;
        }

        public TicketBuilder withOrder(Order order) {
            this.order = order;
            return this;
        }

        public TicketBuilder withUser(ApplicationUser user) {
            this.user = user;
            return this;
        }

        public TicketBuilder withPurchased(Boolean purchased) {
            this.purchased = purchased;
            return this;
        }

        public TicketBuilder withInCart(Boolean inCart) {
            this.inCart = inCart;
            return this;
        }

        public TicketBuilder withTicketUuid(UUID ticketUuid) {
            this.ticketUuid = ticketUuid;
            return this;
        }

        public Ticket build() {
            Ticket ticket = new Ticket();
            ticket.setId(id);
            ticket.setTicketType(ticketType);
            ticket.setPrice(price);
            ticket.setReserved(reserved);
            ticket.setSeat(seat);
            ticket.setStandingSector(standingSector);
            ticket.setShow(show);
            ticket.setOrder(order);
            ticket.setUser(user);
            ticket.setPurchased(purchased);
            ticket.setInCart(inCart);
            ticket.setDate(date);
            ticket.setTicketUuid(ticketUuid);
            return ticket;
        }
    }
}