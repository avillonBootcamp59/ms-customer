package proyecto1.mscustomer.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.server.ResponseStatusException;
import proyecto1.mscustomer.dto.CustomerSummaryDTO;
import proyecto1.mscustomer.entity.Customer;
import proyecto1.mscustomer.service.CustomerService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.web.reactive.function.BodyInserters.fromValue;


@ExtendWith(MockitoExtension.class)
public class CustomerControllerTest {

    WebTestClient webTestClient;

    @Mock
    private CustomerService customerService;

    private Customer mockCustomer;

    @BeforeEach
    void setup() {
        webTestClient = WebTestClient.bindToController(new CustomerController(customerService)).build();
        mockCustomer = new Customer("1", "Juan Pérez", "PERSONAL", "12345678", "juan@email.com", "VIP");
    }

    @Test
    @DisplayName("Obtener todos los clientes - 200 OK")
    void whenGetAllCustomers_thenReturnList() {
        Mockito.when(customerService.listCustomers()).thenReturn(Flux.just(mockCustomer));

        webTestClient.get()
                .uri("/v1.0/customers")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Customer.class)
                .hasSize(1);
    }

    @Test
    @DisplayName("Obtener cliente por ID - 200 OK")
    void whenGetCustomerById_thenReturnCustomer() {
        Mockito.when(customerService.getCustomer(anyString())).thenReturn(Mono.just(mockCustomer));

        webTestClient.get()
                .uri("/v1.0/customers/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Customer.class)
                .isEqualTo(mockCustomer);
    }

    @Test
    @DisplayName("Obtener cliente por ID - 404 NOT FOUND")
    void whenGetCustomerByIdNotFound_thenReturn404() {
        Mockito.when(customerService.getCustomer(anyString()))
                .thenReturn(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado")));

        webTestClient.get()
                .uri("/v1.0/customers/999")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @DisplayName("Crear un nuevo cliente - 200")
    void whenCreateCustomer_thenReturnCreated() {
        Mockito.when(customerService.createCustomer(any(Customer.class))).thenReturn(Mono.just(mockCustomer));

        webTestClient.post()
                .uri("/v1.0/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .body(fromValue(mockCustomer))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Customer.class)
                .isEqualTo(mockCustomer);
    }

    @Test
    @DisplayName("Actualizar cliente - 200 OK")
    void whenUpdateCustomer_thenReturnUpdatedCustomer() {
        Customer updatedCustomer = new Customer("1", "Juan Pérez", "PERSONAL", "12345678", "nuevo@email.com", "VIP");

        Mockito.when(customerService.updateCustomer(anyString(), any(Customer.class)))
                .thenReturn(Mono.just(updatedCustomer));

        webTestClient.put()
                .uri("/v1.0/customers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .body(fromValue(updatedCustomer))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Customer.class)
                .isEqualTo(updatedCustomer);
    }

    @Test
    @DisplayName("Eliminar un cliente - 200 OK")
    void whenDeleteCustomer_thenReturnOk() {
        Mockito.when(customerService.deleteCustomer(anyString())).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/v1.0/customers/1")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("Obtener resumen del cliente - 200 OK")
    void whenGetCustomerSummary_thenReturnSummary() {
        CustomerSummaryDTO summaryDTO = new CustomerSummaryDTO(
                "1", "Juan Pérez", "12345678", "PERSONAL", "VIP", List.of(), List.of()
        );

        Mockito.when(customerService.getCustomerSummary(anyString())).thenReturn(Mono.just(summaryDTO));

        webTestClient.get()
                .uri("/v1.0/customers/summary/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerSummaryDTO.class)
                .isEqualTo(summaryDTO);
    }

    @Test
    @DisplayName("Obtener resumen de cliente no encontrado - 404 NOT FOUND")
    void whenGetCustomerSummaryNotFound_thenReturn404() {
        Mockito.when(customerService.getCustomerSummary(anyString()))
                .thenReturn(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado")));


        webTestClient.get()
                .uri("/v1.0/customers/summary/999")
                .exchange()
                .expectStatus().isNotFound();
    }

}
