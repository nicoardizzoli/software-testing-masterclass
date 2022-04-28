package com.nicoardizzoli.testing.payment.stripe;

import com.nicoardizzoli.testing.payment.CardPaymentCharge;
import com.nicoardizzoli.testing.payment.Currency;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.net.RequestOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

class StripeServiceTest {

    private StripeService underTestStripeService;

    @Mock
    private StripeApi stripeApi;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        underTestStripeService = new StripeService(stripeApi);
    }

    @Test
    void itShouldChargeCard() throws StripeException {
        //Given
        BigDecimal amount = BigDecimal.valueOf(10);
        Currency currency = Currency.USD;
        String cardSourcePrueba = "cardSourcePrueba";
        String description = "Description hello 123";

        Charge charge = new Charge();
        charge.setPaid(true);
        given(stripeApi.create(anyMap(), any())).willReturn(charge);

        //When
        CardPaymentCharge cardPaymentCharge = underTestStripeService.chargeCard(amount, currency, cardSourcePrueba, description);

        //Then
        ArgumentCaptor<Map<String, Object>> paramsArgumentCaptor = ArgumentCaptor.forClass(Map.class);
        ArgumentCaptor<RequestOptions> requestOptionsArgumentCaptor = ArgumentCaptor.forClass(RequestOptions.class);

        then(stripeApi).should().create(paramsArgumentCaptor.capture(), requestOptionsArgumentCaptor.capture());

        assertThat(paramsArgumentCaptor.getValue().keySet()).hasSize(4);

        assertThat(paramsArgumentCaptor.getValue())
                .containsEntry("amount", amount)
                .containsEntry("currency", currency)
                .containsEntry("source", cardSourcePrueba)
                .containsEntry("description", description);

        assertThat(requestOptionsArgumentCaptor.getValue()).isNotNull();
        assertThat(cardPaymentCharge.isCardDebited()).isTrue();


//        params.put("amount", amount);
//        params.put("currency", currency);
//        params.put("source", cardSource);
//        params.put("description", description);

    }
}