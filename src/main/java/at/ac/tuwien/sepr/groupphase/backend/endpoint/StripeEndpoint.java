package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.service.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.security.access.annotation.Secured;

import java.util.Map;

/**
 * Controller for handling Stripe payment operations.
 */
@RestController
@RequestMapping("/api/v1/stripe")
public class StripeEndpoint {

    private final StripeService stripeService;

    @Autowired
    public StripeEndpoint(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    /**
     * Create a PaymentIntent.
     *
     * @param createPaymentRequest details for creating the PaymentIntent
     * @return PaymentIntent client secret
     */
    @PostMapping("/create-payment-intent")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @Operation(summary = "Create a PaymentIntent")
    public ResponseEntity<Map<String, String>> createPaymentIntent(@Valid @RequestBody CreatePaymentRequest createPaymentRequest) {
        try {
            PaymentIntent paymentIntent = stripeService.createPaymentIntent(
                    createPaymentRequest.getAmount(),
                    createPaymentRequest.getCurrency(),
                    createPaymentRequest.getPaymentMethodType()
            );
            return ResponseEntity.ok(Map.of("clientSecret", paymentIntent.getClientSecret()));
        } catch (StripeException e) {
            // Handle Stripe exceptions appropriately
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    // DTO for receiving payment intent creation request
    public static class CreatePaymentRequest {
        private Double amount;
        private String currency;
        private String paymentMethodType;

        // Getters and Setters

        public Double getAmount() {
            return amount;
        }

        public void setAmount(Double amount) {
            this.amount = amount;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getPaymentMethodType() {
            return paymentMethodType;
        }

        public void setPaymentMethodType(String paymentMethodType) {
            this.paymentMethodType = paymentMethodType;
        }
    }
}
