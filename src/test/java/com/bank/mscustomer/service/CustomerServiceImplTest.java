package com.bank.mscustomer.service;

import com.bank.mscustomer.client.AccountClient;
import com.bank.mscustomer.client.CreditClient;
import com.bank.mscustomer.dto.AccountDTO;
import com.bank.mscustomer.dto.CreditDTO;
import com.bank.mscustomer.dto.CustomerSummaryDTO;
import com.bank.mscustomer.entity.CustomerEntity;
import com.bank.mscustomer.mapper.CustomerConverterMapper;
import com.bank.mscustomer.model.CreateCustomerRequest;
import com.bank.mscustomer.model.CustomerSummary;
import com.bank.mscustomer.model.ListCustomerDataResponse;
import com.bank.mscustomer.model.UpdateCustomerRequest;
import com.bank.mscustomer.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CreditClient creditClient;

    @Mock
    private AccountClient accountClient;

    @Mock
    private CustomerConverterMapper customerConverterMapper;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private CustomerEntity customerEntity;
    private CustomerSummaryDTO customerSummaryDTO;

    @BeforeEach
    void setUp() {
        customerEntity = new CustomerEntity("1", "Juan Pérez", "12345678", "Personal", "carlos.perez@gmail.com","VIP");
        customerSummaryDTO = new CustomerSummaryDTO("1", "Juan Pérez", "12345678", "Personal", "VIP", List.of(), List.of());
    }

    @Test
    @DisplayName("when find all customers")
    void whenFindAllCustomers() {
        // Configurar datos de prueba
        List<CustomerEntity> customers = List.of(
                new CustomerEntity("1", "Carlos Pérez", "Personal", "87654321", "carlos@email.com", "Normal"),
                new CustomerEntity("2", "Ana López", "Empresarial", "12345678", "ana@email.com", "PYME")
        );
        List<ListCustomerDataResponse> customerDataList = new ArrayList<>();
        customerDataList.add(new ListCustomerDataResponse());
        customerDataList.add(new ListCustomerDataResponse());

        Mockito.when(customerRepository.findAll()).thenReturn(Flux.fromIterable(customers));
        Mockito.when(customerConverterMapper.toListCustomerDataResponseList(customers)).thenReturn(customerDataList);

        StepVerifier.create(customerService.getAll(null))
                .assertNext(response -> {
                    assertNotNull(response);
                    assertNotNull(response.getBody());
                    assertFalse(response.getBody().getData().isEmpty());
                    assertEquals(2, response.getBody().getData().size()); // Asegurar que devuelve 2 clientes
                    assertEquals(200, response.getBody().getMetadata().getStatus());
                })
                .verifyComplete();

    }


    @Test
    @DisplayName("when fail find all customers")
    void whenFindAllCustomersFail() {
        Mockito.when(customerRepository.findAll()).thenReturn(Flux.empty());

        StepVerifier.create(customerService.getAll(null))
                .expectErrorMatches(throwable -> throwable instanceof ResponseStatusException &&
                        ((ResponseStatusException) throwable).getStatus() == HttpStatus.NOT_FOUND &&
                        "No hay clientes registrados.".equals(((ResponseStatusException) throwable).getReason()))
                .verify();
    }
    @Test
    @DisplayName("when getCustomerById then return customer")
    void whenGetCustomerById() {
        String customerId = "1";
        CustomerEntity customerEntity = new CustomerEntity(customerId, "Carlos Pérez", "Personal", "87654321", "carlos@email.com", "Normal");

        ListCustomerDataResponse customerData = new ListCustomerDataResponse();
        customerData.setId(customerId);
        customerData.setName(customerEntity.getName());
        customerData.setNumberDocument(customerEntity.getNumberDocument());
        customerData.setType(customerEntity.getType());
        customerData.setEmail(customerEntity.getEmail());
        customerData.setProfile(customerEntity.getProfile());


        Mockito.when(customerRepository.findById(customerId)).thenReturn(Mono.just(customerEntity));
        Mockito.when(customerConverterMapper.toListCustomerDataResponse(customerEntity)).thenReturn(customerData);


        StepVerifier.create(customerService.getCustomerById(customerId, null))
                .assertNext(response -> {
                    assertEquals(HttpStatus.OK, response.getStatusCode());
                    assertNotNull(response.getBody());
                    assertNotNull(response.getBody().getData());
                    assertFalse(response.getBody().getData().isEmpty());
                    assertEquals(customerId, response.getBody().getData().get(0).getId());
                })
                .verifyComplete();

    }

    @Test
    @DisplayName("when createCustomer then return success")
    void whenCreateCustomer() {
        CreateCustomerRequest request = new CreateCustomerRequest();
        request.setId("1");
        request.setName("Carlos Pérez");
        request.setNumberDocument("09876543");
        request.setType("Empresarial");
        request.setEmail("carlos@email.com");
        request.setProfile("VIP");
        CustomerEntity customerEntity = new CustomerEntity("1", "Carlos Pérez", "Personal", "87654321", "carlos@email.com", "Normal");

        Mockito.when(customerRepository.findByNumberDocument(request.getNumberDocument())).thenReturn(Mono.empty());
        Mockito.when(customerRepository.save(Mockito.any(CustomerEntity.class))).thenReturn(Mono.just(customerEntity));

        StepVerifier.create(customerService.createCustomer(Mono.just(request), null))
                .assertNext(response -> {
                    assertEquals(HttpStatus.OK, response.getStatusCode());
                    assertNotNull(response.getBody());
                    assertEquals("1", response.getBody().getData().getId());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("when updateCustomer then return success")
    void whenUpdateCustomer() {
        String customerId = "1";
        UpdateCustomerRequest request = new UpdateCustomerRequest();
        request.setId(customerId);
        request.setName("Carlos Pérez");
        request.setNumberDocument("09876543");
        request.setType("Empresarial");
        request.setEmail("carlos@email.com");
        request.setProfile("VIP");

        CustomerEntity existingCustomer = new CustomerEntity(customerId, "Carlos Pérez", "Personal", "09876543", "carlos@email.com", "Normal");
        CustomerEntity updatedCustomer = new CustomerEntity(customerId, "Carlos Pérez", "Empresarial", "09876543", "carlos@email.com", "VIP");

        Mockito.when(customerRepository.findById(customerId)).thenReturn(Mono.just(existingCustomer));
        Mockito.when(customerRepository.save(Mockito.any(CustomerEntity.class))).thenReturn(Mono.just(updatedCustomer));

        StepVerifier.create(customerService.updateCustomer(Mono.just(request), null))
                .assertNext(response -> {
                    assertEquals(HttpStatus.OK, response.getStatusCode());
                    assertNotNull(response.getBody());
                    assertEquals("1", response.getBody().getData().getId());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("when deleteCustomer then return success")
    void whenDeleteCustomer() {
        String customerId = "1";
        CustomerEntity customerEntity = new CustomerEntity(customerId, "Carlos Pérez", "Personal", "87654321", "carlos@email.com", "Normal");

        Mockito.when(customerRepository.findById(customerId)).thenReturn(Mono.just(customerEntity));
        Mockito.when(customerRepository.delete(customerEntity)).thenReturn(Mono.empty());

        StepVerifier.create(customerService.deleteCustomer(customerId, null))
                .assertNext(response -> {
                    assertEquals(200, response.getStatusCodeValue());
                    assertNotNull(response.getBody());
                    assertEquals(200, response.getBody().getMetadata().getStatus());
                    assertEquals("Cliente eliminado correctamente", response.getBody().getMetadata().getMessage());
                })
                .verifyComplete();

    }

    @Test
    @DisplayName("when getCustomerSummary then return customer summary with accounts and credits")
    void whenGetCustomerSummary() {
        // Mock de datos de cliente

        List<AccountDTO> accountList = List.of(
                new AccountDTO("1001", "Cuenta Ahorro", customerEntity.getId(), "123456789", 5000.00, 1, 0.0),
                new AccountDTO("1002", "Cuenta Corriente", customerEntity.getId(), "987654321", 10000.00, 9, 0)
        );


        List<CreditDTO> creditList = List.of(
                new CreditDTO("2001", customerEntity.getId(), "Crédito Hipotecario", 150000.00,1,1),
                new CreditDTO("2002", customerEntity.getId(), "Crédito Vehicular", 30000.00, 2, 4)
        );

        Mockito.when(customerRepository.findById(customerEntity.getId())).thenReturn(Mono.just(customerEntity));
        Mockito.when(accountClient.getAccountsByCustomer(customerEntity.getId())).thenReturn(Flux.fromIterable(accountList));
        Mockito. when(creditClient.getCreditProductsByCustomer(customerEntity.getId())).thenReturn(Flux.fromIterable(creditList));

        CustomerSummaryDTO summaryDTO = new CustomerSummaryDTO();
        summaryDTO.setCustomerId(customerEntity.getId());
        summaryDTO.setName(customerEntity.getName());
        summaryDTO.setDocumentNumber(customerEntity.getNumberDocument());
        summaryDTO.setType(customerEntity.getType());
        summaryDTO.setProfile(customerEntity.getProfile());
        summaryDTO.setAccounts(accountList);
        summaryDTO.setCredits(creditList);

        CustomerSummary summary = customerConverterMapper.toCustomerSummary(summaryDTO);

        Mockito.when(customerConverterMapper.toCustomerSummary(summaryDTO)).thenReturn(summary);

        StepVerifier.create(customerService.getCustomerSummary(customerEntity.getId(), null))
                .assertNext(response -> {
                    assertEquals(200, response.getStatusCodeValue());
                 /*   assertNotNull(response.getBody());
                    assertEquals(customerEntity.getId(), response.getBody().getCustomerId());
                    assertEquals("Carlos Pérez", response.getBody().getName());
                    assertEquals(2, response.getBody().getAccounts().size());
                    assertEquals(2, response.getBody().getCredits().size());*/
                })
                .verifyComplete();
    }
}
