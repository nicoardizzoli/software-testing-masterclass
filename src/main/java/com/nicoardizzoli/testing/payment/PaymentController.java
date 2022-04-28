package com.nicoardizzoli.testing.payment;

import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/payment")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/{customerId}")
    public void makePayment(@RequestBody PaymentRequest paymentRequest, @PathVariable(name = "customerId") UUID customerId) {
        paymentService.chargeCard(customerId, paymentRequest);
    }

    @GetMapping("/{paymentId}")
    public Payment getPaymentById(@PathVariable(name = "paymentId") Long paymentId) {
        return paymentService.getPaymentById(paymentId);
    }


    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
}
