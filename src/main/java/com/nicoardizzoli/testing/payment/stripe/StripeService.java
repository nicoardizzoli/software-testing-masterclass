package com.nicoardizzoli.testing.payment.stripe;

import com.nicoardizzoli.testing.payment.CardPaymentCharge;
import com.nicoardizzoli.testing.payment.CardPaymentCharger;
import com.nicoardizzoli.testing.payment.Currency;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.net.RequestOptions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@ConditionalOnProperty(value = "stripe.enabled", havingValue = "true")
public class StripeService implements CardPaymentCharger {

    private final StripeApi stripeApi;

    private static final RequestOptions requestOptions = RequestOptions.builder()
            .setApiKey("sk_test_4eC39HqLyjWDarjtT1zdp7dc")
            .build();

    @Override
    public CardPaymentCharge chargeCard(BigDecimal amount, Currency currency, String cardSource, String description) {

        Map<String, Object> params = new HashMap<>();
        params.put("amount", amount);
        params.put("currency", currency);
        params.put("source", cardSource);
        params.put("description", description);

        try {
            Charge charge = stripeApi.create(params, requestOptions);
            Boolean paid = charge.getPaid();
            return new CardPaymentCharge(paid);
        } catch (StripeException e) {
            throw new IllegalStateException("Cannot make stripe charge", e);
        }
    }

    public StripeService(StripeApi stripeApi) {
        this.stripeApi = stripeApi;
    }
}
