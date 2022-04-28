package com.nicoardizzoli.testing.customer;

import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerRegistrationService {

    private final CustomerRepository customerRepository;

    public void registerNewCustomer(CustomerRegistrationRequest request){
        //1. phonenumber is taken
        //2. if taken lets check if belongs to same customer
        //2.1 if yes return
        //2.2 thrown an exception
        //3 save a customer
        Optional<Customer> customerByPhoneNumber = customerRepository.findCustomerByPhoneNumber(request.getCustomer().getPhoneNumber());
        if (customerByPhoneNumber.isPresent()) {
            Customer customer = customerByPhoneNumber.get();
            if (customer.getName().equals(request.getCustomer().getName())){
                return;
            }
            throw new IllegalArgumentException(String.format("phone number %s is taken", customer.getPhoneNumber()));
        }

        if (request.getCustomer().getId() == null) request.getCustomer().setId(UUID.randomUUID());

        customerRepository.save(request.getCustomer());
    }


    public CustomerRegistrationService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }
}
