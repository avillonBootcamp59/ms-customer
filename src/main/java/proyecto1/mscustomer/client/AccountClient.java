package proyecto1.mscustomer.client;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import proyecto1.mscustomer.dto.AccountDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class  AccountClient {

  private final WebClient webClient;

  public AccountClient(WebClient.Builder webClientBuilder) {
    this.webClient = webClientBuilder.baseUrl("http://localhost:8082").build();
  }
  public Flux<AccountDTO> getAccountsByCustomer(String customerId) {
    return webClient.get()
        .uri("/v1.0/accounts/customer/{id}", customerId)
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError, response ->
                Mono.error(new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No se encontraron cuentas para este cliente")))
        .onStatus(HttpStatus::is5xxServerError, response ->
                Mono.error(new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR, "Error en el servicio ms-accounts")))
        .bodyToFlux(AccountDTO.class);
  }
}