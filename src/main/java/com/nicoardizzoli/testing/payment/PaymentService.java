package com.nicoardizzoli.testing.payment;

import com.nicoardizzoli.testing.customer.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {

    private final CustomerRepository customerRepository;
    private final PaymentRepository paymentRepository;
    private final CardPaymentCharger cardPaymentCharger;


    void chargeCard(UUID customerId, PaymentRequest request) {
        //1. customer exist if not throw
        customerRepository.findById(customerId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        //2. support currency if not throw
        Currency currency1 = request.getPayment().getCurrency();
        boolean isCurrencySupported = Arrays.stream(Currency.values()).anyMatch(currency -> currency.equals(currency1));
        if (!isCurrencySupported) {
            throw new IllegalArgumentException("currency not suported: " + currency1);
        }

        //3. charge card
        CardPaymentCharge cardPaymentCharge =
                cardPaymentCharger.chargeCard(
                        request.getPayment().getAmount(),
                        request.getPayment().getCurrency(),
                        request.getPayment().getSource(),
                        request.getPayment().getDescription()
                );

        //4. if not debited throw
        if (!cardPaymentCharge.isCardDebited()) {
            throw new IllegalStateException(String.format("Card not debited for customer %s", customerId));
        }

        //5. insert payment
        request.getPayment().setCustomerId(customerId);
        paymentRepository.save(request.getPayment());
        //6. TODO: send sms
    }

    public Payment getPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId).orElseThrow(() -> new IllegalArgumentException("payment not found"));
    }


    public PaymentService(CustomerRepository customerRepository, PaymentRepository paymentRepository, CardPaymentCharger cardPaymentCharger) {
        this.customerRepository = customerRepository;
        this.paymentRepository = paymentRepository;
        this.cardPaymentCharger = cardPaymentCharger;
    }
}
