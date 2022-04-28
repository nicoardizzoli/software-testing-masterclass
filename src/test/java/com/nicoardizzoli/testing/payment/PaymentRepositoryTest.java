package com.nicoardizzoli.testing.payment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(properties = {
        "spring.jpa.properties.javax.persistence.validation.mode=none"
})
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository underTestPaymentRepository;

    @BeforeEach
    void setUp() {
    }

    @Test
    void itShouldInsertPayment() {
        //Given
        Payment payment = new Payment(1L, UUID.randomUUID(), BigDecimal.valueOf(100),Currency.USD, "source test 123", "donation");
        //When
        underTestPaymentRepository.save(payment);

        //Then
        Optional<Payment> paymentById = underTestPaymentRepository.findById(1L);
        assertThat(paymentById).isPresent().hasValueSatisfying(payment1 -> assertThat(payment1).isEqualTo(payment));

    }
}