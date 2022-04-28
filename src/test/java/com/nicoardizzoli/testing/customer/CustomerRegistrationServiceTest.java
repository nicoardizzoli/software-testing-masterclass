package com.nicoardizzoli.testing.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


//LO QUE QUEREMOS TESTEAR CON LAS UNIT TEST SON LAS UNIDADES EN SI, AISLADAS; POR ESO MOCKEAMOS EL REPOSITORY Y TESTEAMOS EL SERVICE.
//PARA SABER SI EL SERVICIO ESTA TESTEADO COMPLETAMENTE, CORREMOS EL TEST CON "Coverage"! (click en el nombre del test)
class CustomerRegistrationServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Captor
    private ArgumentCaptor<Customer> customerArgumentCaptor;

    private CustomerRegistrationService underTestCustomerRegistrationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        underTestCustomerRegistrationService = new CustomerRegistrationService(customerRepository);
    }

    @Test
    void itShouldRegisterNewCustomer() {
        //Given
        String phoneNumber = "000099";
        Customer customer = new Customer(UUID.randomUUID(), "Maria", phoneNumber);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        // cuando se llame al metodo con este numero, va a retornar que no hay customer con ese telefono
        BDDMockito.given(customerRepository.findCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.empty());

        //When
        underTestCustomerRegistrationService.registerNewCustomer(request);

        //Then
        BDDMockito.then(customerRepository).should().save(customerArgumentCaptor.capture());
        Customer customerArgumentCaptorValue = customerArgumentCaptor.getValue();
        assertThat(customerArgumentCaptorValue).isEqualTo(customer);
    }

    @Test
    void itShouldNotRegisterCustomerWhenCustomerExist() {
        //Given
        String phoneNumber = "000099";
        Customer customer = new Customer(UUID.randomUUID(), "Maria", phoneNumber);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        // cuando se llame al metodo con este numero, va a retornar que el mismo customer que existe
        BDDMockito.given(customerRepository.findCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.of(customer));

        //When
        underTestCustomerRegistrationService.registerNewCustomer(request);

        //Then
        //OPCION 1: nunca se deberia llamar al save
//        BDDMockito.then(customerRepository).should(Mockito.never()).save(Mockito.any());

        //OPCION 2: lo mismo pero de otra manera
        //en todo el metodo se llama unicamente al findcustomer, y desp no deberia tener mas interacciones.
        BDDMockito.then(customerRepository).should().findCustomerByPhoneNumber(phoneNumber);
        BDDMockito.then(customerRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void itShouldNotRegisterCustomerWhenPhoneNumberIsTaken() {
        //Given
        String phoneNumber = "000099";
        Customer customer = new Customer(UUID.randomUUID(), "Maria", phoneNumber);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        // cuando se llame al metodo con este numero, va a retornar un customer que no es el mismo que estamos intentando guardar
        BDDMockito.given(customerRepository.findCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.of(new Customer(UUID.randomUUID(), "Roberto", phoneNumber)));

        //When
        //Then
        // Cuando el metodo es llamado, tiene que tirar IllegalArgumentException por eso el when/then juntos
        assertThatThrownBy(() -> underTestCustomerRegistrationService.registerNewCustomer(request))
                                    .isInstanceOf(IllegalArgumentException.class)
                                    .hasMessage(String.format("phone number %s is taken", phoneNumber));

        //Finally
        BDDMockito.then(customerRepository).should(Mockito.never()).save(Mockito.any(Customer.class));
    }

    @Test
    void itShouldRegisterNewCustomerWhenIdIsNull() {
        //Given
        String phoneNumber = "000099";
        Customer customer = new Customer(null, "Maria", phoneNumber);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        // cuando se llame al metodo con este numero, va a retornar que no hay customer con ese telefono
        BDDMockito.given(customerRepository.findCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.empty());

        //When
        underTestCustomerRegistrationService.registerNewCustomer(request);

        //Then
        BDDMockito.then(customerRepository).should().save(customerArgumentCaptor.capture());
        Customer customerArgumentCaptorValue = customerArgumentCaptor.getValue();
            //el id lo generamos por lo q viene null y vuelve con id.
        assertThat(customerArgumentCaptorValue).usingRecursiveComparison().ignoringFields("id");
        assertThat(customerArgumentCaptorValue.getId()).isNotNull();
    }


}