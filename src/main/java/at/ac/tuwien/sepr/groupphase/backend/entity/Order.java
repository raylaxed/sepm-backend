package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

@Entity
@Table(name = "purchase_order") // Using purchase_order as table name since 'order' is a reserved keyword
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double total;

    @Column(nullable = false)
    private LocalDateTime orderDate;

    @OneToMany(mappedBy = "order")
    private List<Ticket> tickets = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    private ApplicationUser user;

    @Column(name = "payment_intent_id", nullable = false)
    private String paymentIntentId;

    @Column(nullable = false)
    private Boolean cancelled = false;

    @Column(name = "invoice_path")
    private String invoicePath;

    @Column(name = "cancellation_invoice_id")
    private Long cancellationInvoiceId;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    public ApplicationUser getUser() {
        return user;
    }

    public void setUser(ApplicationUser user) {
        this.user = user;
    }

    public String getPaymentIntentId() {
        return paymentIntentId;
    }

    public void setPaymentIntentId(String paymentIntentId) {
        this.paymentIntentId = paymentIntentId;
    }

    public String getInvoicePath() {
        return invoicePath;
    }

    public void setInvoicePath(String invoicePath) {
        this.invoicePath = invoicePath;
    }

    public Boolean getCancelled() {
        return cancelled;
    }

    public void setCancelled(Boolean cancelled) {
        this.cancelled = cancelled;
    }

    public Long getCancellationInvoiceId() {
        return cancellationInvoiceId;
    }

    public void setCancellationInvoiceId(Long cancellationInvoiceId) {
        this.cancellationInvoiceId = cancellationInvoiceId;
    }

    @Override
    public String toString() {
        return "Order{"
            + "id=" + id
            + ", total=" + total
            + ", orderDate=" + orderDate
            + ", paymentIntentId=" + paymentIntentId
            + ", cancelled=" + cancelled
            + ", invoicePath='" + invoicePath + '\''
            + ", cancellationInvoiceId=" + cancellationInvoiceId
            + '}';
    }

    // Builder pattern
    public static final class OrderBuilder {
        private Long id;
        private Double total;
        private LocalDateTime orderDate;
        private List<Ticket> tickets = new ArrayList<>();
        private String paymentIntentId;
        private ApplicationUser user;
        private Boolean cancelled = false;
        private String invoicePath;
        private Long cancellationInvoiceId;

        private OrderBuilder() {
        }

        public static OrderBuilder anOrder() {
            return new OrderBuilder();
        }

        public OrderBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public OrderBuilder withTotal(Double total) {
            this.total = total;
            return this;
        }

        public OrderBuilder withOrderDate(LocalDateTime orderDate) {
            this.orderDate = orderDate;
            return this;
        }

        public OrderBuilder withTickets(List<Ticket> tickets) {
            this.tickets = tickets;
            return this;
        }

        public OrderBuilder withPaymentIntentId(String paymentIntentId) {
            this.paymentIntentId = paymentIntentId;
            return this;
        }

        public OrderBuilder withUser(ApplicationUser user) {
            this.user = user;
            return this;
        }

        public OrderBuilder withInvoicePath(String invoicePath) {
            this.invoicePath = invoicePath;
            return this;
        }

        public OrderBuilder withCancelled(Boolean cancelled) {
            this.cancelled = cancelled;
            return this;
        }

        public OrderBuilder withCancellationInvoiceId(Long cancellationInvoiceId) {
            this.cancellationInvoiceId = cancellationInvoiceId;
            return this;
        }

        public Order build() {
            Order order = new Order();
            order.setId(id);
            order.setTotal(total);
            order.setOrderDate(orderDate);
            order.setTickets(tickets);
            order.setPaymentIntentId(paymentIntentId);
            order.setUser(user);
            order.setCancelled(cancelled);
            order.setInvoicePath(invoicePath);
            order.setCancellationInvoiceId(cancellationInvoiceId);
            return order;
        }
    }
}