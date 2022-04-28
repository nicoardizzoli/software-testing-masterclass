package com.nicoardizzoli.testing.customer;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

//ojo que esta annotation es clave para testear los repositorys, y el properties es para que tome las validaciones de @Column
@DataJpaTest(properties = {
        "spring.jpa.properties.javax.persistence.validation.mode=none"
    })
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository underTestCustomerRepository;

    //PARA CONFIGURAR EL INSERT DE LOS TEST ASI, VAMOS A GENERATE, TEST METHOD, EDIT TEMPLATE Y LE PONEMOS EL itShould adelante del nombre y el G-W-T en el body
    @Test
    void itShouldFindCustomerByPhoneNumber() {
        //Given
        UUID id = UUID.randomUUID();
        String name = "Nicolas Ardizzoli";
        String phoneNumber = "2216554175";
        Customer customer = new Customer(id, name, phoneNumber);
        underTestCustomerRepository.save(customer);

        //When
        Optional<Customer> customerByPhoneNumber = underTestCustomerRepository.findCustomerByPhoneNumber(phoneNumber);

        //Then
        Assertions.assertThat(customerByPhoneNumber).isPresent()
                .hasValueSatisfying(c -> {
                    assertThat(c).usingRecursiveComparison().isEqualTo(customer);
                });
    }

    @Test
    void itShouldSaveCustomer() {
        //Given
        UUID id = UUID.randomUUID();
        String name = "Nicolas Ardizzoli";
        String phoneNumber = "2216554175";
        Customer customer = new Customer(id, name, phoneNumber);

        //When
        underTestCustomerRepository.save(customer);

        //Then
        Optional<Customer> customerById = underTestCustomerRepository.findById(id);

        Assertions.assertThat(customerById).isPresent()
                .hasValueSatisfying(c -> {
//                    assertThat(c.getId()).isEqualTo(id);
//                    assertThat(c.getName()).isEqualTo(name);
//                    assertThat(c.getPhoneNumber()).isEqualTo(phoneNumber);

                    //compara campo por campo
                    assertThat(c).usingRecursiveComparison().isEqualTo(customer);
                });
    }

    @Test
    void itShouldNotSaveCustomerWhenNameIsNull() {
        //Given
        UUID id = UUID.randomUUID();
        String phoneNumber = "2216554173";
        Customer customer = new Customer(id, null, phoneNumber);

        //When
        //Then
        assertThatThrownBy(() -> underTestCustomerRepository.save((customer)))
                .hasMessage("not-null property references a null or transient value : com.nicoardizzoli.testing.customer.Customer.name; nested exception is org.hibernate.PropertyValueException: not-null property references a null or transient value : com.nicoardizzoli.testing.customer.Customer.name")
                .isInstanceOf(DataIntegrityViolationException.class);

    }

    @Test
    void itShouldNotSaveCustomerWhenPhoneNumberIsNull() {
        //Given
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id, "Nicolas", null);

        //When
        //Then
        assertThatThrownBy(() -> underTestCustomerRepository.save((customer)))
                .hasMessage("not-null property references a null or transient value : com.nicoardizzoli.testing.customer.Customer.phoneNumber; nested exception is org.hibernate.PropertyValueException: not-null property references a null or transient value : com.nicoardizzoli.testing.customer.Customer.phoneNumber")
                .isInstanceOf(DataIntegrityViolationException.class);

    }

}