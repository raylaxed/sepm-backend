package at.ac.tuwien.sepr.groupphase.backend.service;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

public interface StripeService {

    /**
     * Creates a payment intent for the given amount, currency, and payment method type.
     *
     * @param amount the amount to be charged
     * @param currency the currency to be charged in
     * @param paymentMethodType the type of payment method to be used
     * @return the created payment intent
     * @throws StripeException if there is an error creating the payment intent
     */
    PaymentIntent createPaymentIntent(Double amount, String currency, String paymentMethodType) throws StripeException;

    /**
     * Refunds a payment for the given payment intent ID and amount.
     *
     * @param paymentIntentId the ID of the payment intent to be refunded
     * @param amount the amount to be refunded
     * @throws StripeException if there is an error refunding the payment
     */
    void refundPayment(String paymentIntentId, Double amount) throws StripeException;
}
