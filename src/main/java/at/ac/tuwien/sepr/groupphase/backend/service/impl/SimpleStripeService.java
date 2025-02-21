package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.service.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SimpleStripeService implements StripeService {

    @Override
    public PaymentIntent createPaymentIntent(Double amount, String currency, String paymentMethodType) throws StripeException {
        PaymentIntentCreateParams params =
                PaymentIntentCreateParams.builder()
                        .setAmount((long) (amount * 100)) // Convert to smallest currency unit
                        .setCurrency(currency)
                        .addPaymentMethodType(paymentMethodType)
                        .build();

        return PaymentIntent.create(params);
    }

    @Override
    public void refundPayment(String paymentIntentId, Double amount) throws StripeException {
        Map<String, Object> params = new HashMap<>();
        params.put("payment_intent", paymentIntentId);
        params.put("amount", (long) (amount * 100)); // Convert to cents

        Refund.create(params);
    }
}
