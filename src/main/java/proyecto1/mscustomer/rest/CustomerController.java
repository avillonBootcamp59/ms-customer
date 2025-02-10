package proyecto1.mscustomer.rest;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import proyecto1.mscustomer.entity.Customer;
import proyecto1.mscustomer.repository.CustomerRepository;

@RestController
@RequestMapping("/v1.0/customers")
@RequiredArgsConstructor
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);
    private final CustomerRepository repository;

    @GetMapping
    public Flux<Customer> getAllCustomers() {
        logger.info("Obteniendo todos los clientes");
        return repository.findAll()
                .doOnNext(customer -> logger.debug("Cliente encontrado: {}", customer));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Customer>> getCustomerById(@PathVariable String id) {
        logger.info("Buscando cliente con ID: {}", id);
        return repository.findById(id)
                .map(customer -> {
                    logger.info("Cliente encontrado: {}", customer);
                    return ResponseEntity.ok(customer);
                })
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<Customer>> createCustomer(@RequestBody Customer customer) {
        logger.info("Creando nuevo cliente: {}", customer);
        return repository.save(customer)
                .map(savedCustomer -> {
                    logger.info("Cliente creado con éxito: {}", savedCustomer);
                    return ResponseEntity.ok(savedCustomer);
                });
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Customer>> updateCustomer(@PathVariable String id, @RequestBody Customer updatedCustomer) {
        logger.info("Actualizando cliente con ID: {}", id);
        return repository.findById(id)
                .flatMap(existingCustomer -> {
                    existingCustomer.setName(updatedCustomer.getName());
                    existingCustomer.setType(updatedCustomer.getType());
                    existingCustomer.setNumberDocument(updatedCustomer.getNumberDocument());
                    existingCustomer.setEmail(updatedCustomer.getEmail());
                    return repository.save(existingCustomer);
                })
                .map(updated -> {
                    logger.info("Cliente actualizado: {}", updated);
                    return ResponseEntity.ok(updated);
                })
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteCustomer(@PathVariable String id) {
        logger.info("Eliminando cliente con ID: {}", id);
        return repository.findById(id)
                .flatMap(existingCustomer ->
                        repository.delete(existingCustomer)
                                .then(Mono.just(ResponseEntity.ok().<Void>build()))
                )
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}
