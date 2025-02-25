package com.bank.mscustomer.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {
/*
    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CreditClient creditClient;

    @Mock
    private AccountClient accountClient;

    @InjectMocks
    private CustomerServiceImpl customerService;



    @Test
    @DisplayName("when add customer")
    void whenAddCustomerOk() {
        CustomerEntity customer = new CustomerEntity();
        customer.setName("Vanesa Juarez");
        customer.setType("Personal");
        customer.setNumberDocument("12093345");
        customer.setEmail("vj@gmail.com");
        customer.setProfile("VIP");

        // Mock para que NO encuentre un cliente con el mismo documento (debe devolver Mono.empty())**
        Mockito.when(customerRepository.findByNumberDocument(customer.getNumberDocument()))
                .thenReturn(Mono.empty());

        Mockito.when(customerRepository.save(any(CustomerEntity.class)))
                .thenReturn(Mono.just(customer));

        StepVerifier.create(customerService.createCustomer(customer))
                .expectNext(customer)
                .verifyComplete();
    }
    @ParameterizedTest
    @CsvSource({
            "1",
            "2"
    })
    @DisplayName("when get Customer by id ok")
    void whenGetCustomerByIdOk(String id) {
        CustomerEntity customer = new CustomerEntity();
        customer.setName("Vanesa Juarez");
        customer.setType("Personal");
        customer.setNumberDocument("12093345");
        customer.setEmail("vj@gmail.com");
        customer.setProfile("VIP");
        customer.setId(id);
        Mockito.when(customerRepository.findById(anyString()))
                .thenReturn(Mono.just(customer));

        StepVerifier.create(customerService.getCustomer(id))
                .assertNext(prod -> assertEquals(prod.getName(),"Vanesa Juarez"))
                .verifyComplete();
    }

    @Test
    @DisplayName("when delete customer successfully")
    void whenDeleteCustomerOk() {
        String customerId = "123";
        Mockito.when(customerRepository.findById(customerId))
                .thenReturn(Mono.just(new CustomerEntity()));

        Mockito.when(customerRepository.deleteById(customerId))
                .thenReturn(Mono.empty());

        StepVerifier.create(customerService.deleteCustomer(customerId))
                .verifyComplete();

        Mockito.verify(customerRepository).deleteById(customerId);
    }
    @Test
    @DisplayName("when find all customers")
    void whenFindAllCustomers() {
        List<CustomerEntity> customers = List.of(
                new CustomerEntity("1", "Carlos Pérez", "Personal", "87654321", "carlos@email.com", "Normal"),
                new CustomerEntity("2", "Ana López", "Empresarial", "12345678", "ana@email.com", "PYME")
        );

        Mockito.when(customerRepository.findAll())
                .thenReturn(Flux.fromIterable(customers));

        StepVerifier.create(customerService.listCustomers())
                .expectNextCount(2)
                .verifyComplete();
    }
    @Test
    @DisplayName("when update customer ok")
    void whenUpdateCustomerOk() {
        String customerId = "123";
        CustomerEntity existingCustomer = new CustomerEntity(customerId, "Carlos Pérez", "Personal", "87654321", "carlos@email.com", "Normal");
        CustomerEntity updatedCustomer = new CustomerEntity(customerId, "Carlos Pérez", "Personal", "87654321", "carlos.new@email.com", "VIP");

        Mockito.when(customerRepository.findById(customerId))
                .thenReturn(Mono.just(existingCustomer));

        Mockito.when(customerRepository.save(any(CustomerEntity.class)))
                .thenReturn(Mono.just(updatedCustomer));

        StepVerifier.create(customerService.updateCustomer(customerId,updatedCustomer))
                .expectNext(updatedCustomer)
                .verifyComplete();
    }

    @Test
    @DisplayName("when getCustomerSummary returns data successfully")
    void whenGetCustomerSummaryReturnsData() {
        String customerId = "123";
        CustomerEntity customer = new CustomerEntity(customerId, "Carlos Pérez", "Personal", "87654321", "carlos@email.com", "Normal");

        List<AccountDTO> accounts = List.of(new AccountDTO("1","", "AHORRO", customerId, 1000.0, 1, 0.0));
        List<CreditDTO> credits = List.of(new CreditDTO("1", customerId, "TARJETA_CREDITO", 5000.0, 10000.0, 15.5));

        Mockito.when(customerRepository.findById(customerId)).thenReturn(Mono.just(customer));
        Mockito.when(accountClient.getAccountsByCustomer(customerId)).thenReturn(Flux.fromIterable(accounts));
        Mockito.when(creditClient.getCreditProductsByCustomer(customerId)).thenReturn(Flux.fromIterable(credits));

        StepVerifier.create(customerService.getCustomerSummary(customerId))
                .assertNext(summary -> {
                    assertEquals(1, summary.getAccounts().size());
                    assertEquals(1, summary.getCredits().size());
                })
                .verifyComplete();

    }

    @Test
    @DisplayName("when get customer by id fails")
    void whenGetCustomerByIdFails() {
        String customerId = "999";

        Mockito.when(customerRepository.findById(anyString()))
                .thenReturn(Mono.empty()); // Simula que el cliente no existe

        StepVerifier.create(customerService.getCustomer(customerId))
                .expectErrorMatches(throwable -> throwable instanceof ResponseStatusException &&
                        ((ResponseStatusException) throwable).getRawStatusCode() == HttpStatus.NOT_FOUND.value())
                .verify();
    }

    @Test
    @DisplayName("when getCustomerSummary fails (customer not found)")
    void whenGetCustomerSummaryFails() {
        String customerId = "999";

        Mockito.when(customerRepository.findById(customerId))
                .thenReturn(Mono.empty()); // Simulamos que el cliente no existe

        StepVerifier.create(customerService.getCustomerSummary(customerId))
                .expectErrorMatches(throwable -> throwable instanceof ResponseStatusException &&
                        ((ResponseStatusException) throwable).getRawStatusCode() == HttpStatus.NOT_FOUND.value())
                .verify();
    }

    @Test
    @DisplayName("when create customer fails (customer already exists)")
    void whenCreateCustomerFails() {
        CustomerEntity customer = new CustomerEntity();
        customer.setNumberDocument("12093345");

        // Simulamos que el cliente ya existe
        Mockito.when(customerRepository.findByNumberDocument(customer.getNumberDocument()))
                .thenReturn(Mono.just(customer));

        StepVerifier.create(customerService.createCustomer(customer))
                .expectErrorMatches(throwable -> throwable instanceof ResponseStatusException &&
                        ((ResponseStatusException) throwable).getRawStatusCode() == HttpStatus.BAD_REQUEST.value())
                .verify();
    }
*/
}
