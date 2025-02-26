package com.bank.mscustomer.client;

import com.bank.mscustomer.dto.AccountDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class  AccountClient {
  private static final Logger logger = LoggerFactory.getLogger(AccountClient.class);
  private final WebClient webClient;

  public AccountClient(WebClient.Builder webClientBuilder) {
    this.webClient = webClientBuilder.baseUrl("http://localhost:8082").build();
  }

  @CircuitBreaker(name = "customerService", fallbackMethod = "fallbackGetAccountCustomerById")
  @TimeLimiter(name = "customerService")
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
  private Flux<AccountDTO> fallbackGetAccountCustomerById(String customerId, Throwable ex) {
    logger.warn("Fallback activado para getAccountsByCustomer debido a error: {}", ex.getMessage());
    return Flux.just(new AccountDTO("fallback", customerId, "Cuenta no disponible", "", 0, 0, 0.0));
  }

}