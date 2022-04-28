package com.nicoardizzoli.testing.payment.stripe;

import com.nicoardizzoli.testing.payment.CardPaymentCharge;
import com.nicoardizzoli.testing.payment.CardPaymentCharger;
import com.nicoardizzoli.testing.payment.Currency;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
//@ConditionalOnProperty(value = "stripe.enabled", havingValue = "false") //no me funcan las custom anotations
@Primary
public class MockStripeService implements CardPaymentCharger {

    @Override
    public CardPaymentCharge chargeCard(BigDecimal amount, Currency currency, String cardSource, String description) {
        return new CardPaymentCharge(true);
    }
}
