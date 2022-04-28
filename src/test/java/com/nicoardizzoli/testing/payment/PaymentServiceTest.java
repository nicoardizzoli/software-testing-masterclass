package com.nicoardizzoli.testing.payment;

import com.nicoardizzoli.testing.customer.Customer;
import com.nicoardizzoli.testing.customer.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

class PaymentServiceTest {

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private CardPaymentCharger cardPaymentCharger;
    private PaymentService underTestPaymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        underTestPaymentService = new PaymentService(customerRepository, paymentRepository, cardPaymentCharger);
    }

    @Test
    void itShouldChargeCardSuccessfully() {
        //Given
        UUID customerId = UUID.randomUUID();
        Currency currency = Currency.USD;
        PaymentRequest paymentRequest = new PaymentRequest(new Payment(1L, null, BigDecimal.valueOf(100), currency, "card1234", "donation"));

        //cuando la clase que devuelve el metodo, no se utiliza, la podemos mockear.
        given(customerRepository.findById(customerId)).willReturn(Optional.of(mock(Customer.class)));

        given(cardPaymentCharger.chargeCard(paymentRequest.getPayment().getAmount(),
                paymentRequest.getPayment().getCurrency(),
                paymentRequest.getPayment().getSource(),
                paymentRequest.getPayment().getDescription()
        )).willReturn(new CardPaymentCharge(true));
        //When
        underTestPaymentService.chargeCard(customerId, paymentRequest);

        //Then
        ArgumentCaptor<Payment> paymentArgumentCaptor = ArgumentCaptor.forClass(Payment.class);
        then(paymentRepository).should().save(paymentArgumentCaptor.capture());

        paymentArgumentCaptor.getValue().getCustomerId().equals(customerId);
        assertThat(paymentArgumentCaptor).usingRecursiveComparison().ignoringFields("customerId");
        assertThat(paymentArgumentCaptor.getValue().getCustomerId()).isEqualTo(customerId);
    }

    @Test
    void itShouldNotChargeAndThrowCardNotDebitedForCustomer() {
        //Given
        UUID customerId = UUID.randomUUID();
        Currency currency = Currency.USD;
        PaymentRequest paymentRequest = new PaymentRequest(new Payment(1L, null, BigDecimal.valueOf(100), currency, "card1234", "donation"));

        //cuando la clase que devuelve el metodo, no se utiliza, la podemos mockear.
        given(customerRepository.findById(customerId)).willReturn(Optional.of(mock(Customer.class)));

        given(cardPaymentCharger.chargeCard(paymentRequest.getPayment().getAmount(),
                paymentRequest.getPayment().getCurrency(),
                paymentRequest.getPayment().getSource(),
                paymentRequest.getPayment().getDescription()
        )).willReturn(new CardPaymentCharge(false));

        //When
        //Then
        assertThatThrownBy(() -> underTestPaymentService.chargeCard(customerId, paymentRequest)).isInstanceOf(IllegalStateException.class).hasMessage("Card not debited for customer " + customerId);
        then(paymentRepository).should(never()).save(any(Payment.class));

    }

    @Test
    void itShouldNotChargeCardAndThrowCurrencyNotSupported() {
        //Given
        UUID customerId = UUID.randomUUID();
        Currency currency = null;
        PaymentRequest paymentRequest = new PaymentRequest(new Payment(1L, null, BigDecimal.valueOf(100), currency, "card1234", "donation"));

        //cuando la clase que devuelve el metodo, no se utiliza, la podemos mockear.
        given(customerRepository.findById(customerId)).willReturn(Optional.of(mock(Customer.class)));

        //When
        assertThatThrownBy(() -> underTestPaymentService.chargeCard(customerId, paymentRequest)).isInstanceOf(IllegalArgumentException.class).hasMessage("currency not suported: " + currency);

        //Then
        then(cardPaymentCharger).shouldHaveNoInteractions();
        then(paymentRepository).should(never()).save(any(Payment.class));

    }
}