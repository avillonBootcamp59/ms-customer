package proyecto1.mscustomer.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import proyecto1.mscustomer.entity.Customer;
import proyecto1.mscustomer.repository.CustomerRepository;

@RestController
@RequestMapping("/v1.0/customers")
@Tag(name = "Customer API", description = "Gestión de Clientes")
@RequiredArgsConstructor
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);
    private final CustomerRepository repository;

    @Operation(summary = "Obtener todos los clientes", description = "Lista todos los clientes registrados en el banco")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Clientes obtenidos correctamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public Flux<Customer> getAllCustomers() {
        return repository.findAll();
    }

    @Operation(summary = "Obtener cliente por ID", description = "Busca un cliente por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Customer>> getCustomerById(@PathVariable String id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear un nuevo cliente", description = "Registra un nuevo cliente en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cliente creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error en la solicitud, cliente ya existe"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public Mono<ResponseEntity<Customer>> createCustomer(@RequestBody @NotNull Customer customer) {
        return repository.findByNumberDocument(customer.getNumberDocument())
                .flatMap(existing -> Mono.just(ResponseEntity.badRequest()
                        .body(existing)))
                .switchIfEmpty(repository.save(customer)
                        .map(savedCustomer -> ResponseEntity.status(HttpStatus.CREATED)
                                .body(savedCustomer)))
                .onErrorResume(error -> Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).build())); // Manejo de errores
    }

    @Operation(summary = "Actualiza un cliente", description = "Actualiza datos de un cliente en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente actualizado correctamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Customer.class))),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
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

    @Operation(summary = "Eliminar un cliente", description = "Elimina un cliente de la base de datos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
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
