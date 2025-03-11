package com.bank.pe.mscustomer.client;

import com.bank.pe.mscustomer.dto.CreditDTO;
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
public class CreditClient {

  private final WebClient webClient;
  private static final Logger logger = LoggerFactory.getLogger(CreditClient.class);
  public CreditClient(WebClient.Builder webClientBuilder) {
    this.webClient = webClientBuilder.baseUrl("http://localhost:8083").build();
  }
  @CircuitBreaker(name = "customerService", fallbackMethod = "fallbackGetCreditProductsByCustomer")
  @TimeLimiter(name = "customerService")
  public Flux<CreditDTO> getCreditProductsByCustomer(String customerId) {
    return webClient.get()
        .uri("/v1.0/credits/customer/{id}", customerId)
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError, response ->
                Mono.error(new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No se encontraron créditos para este cliente")))
        .onStatus(HttpStatus::is5xxServerError, response ->
                Mono.error(new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR, "Error en el servicio ms-credits")))
        .bodyToFlux(CreditDTO.class);
  }

  private Flux<CreditDTO> fallbackGetCreditProductsByCustomer(String customerId, Throwable ex) {
    logger.warn("Fallback activado para getAccountsByCustomer debido a error: {}", ex.getMessage());
    return Flux.just(new CreditDTO("fallback", customerId, "Cuenta no disponible", 0, 0, 0.0));
  }


}
