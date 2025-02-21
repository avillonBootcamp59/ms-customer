package proyecto1.mscustomer.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyecto1.mscustomer.dto.CustomerSummaryDTO;
import proyecto1.mscustomer.entity.Customer;
import proyecto1.mscustomer.service.CustomerService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/v1.0/customers")
@Tag(name = "Customer API", description = "Gestión de Clientes")
@RequiredArgsConstructor
public class CustomerController {

  private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);
  private final CustomerService customerService;

  @Operation(summary = "Obtener todos los clientes", description = "Lista todos los clientes registrados en el banco")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Clientes obtenidos correctamente"),
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
  })
  @GetMapping
  public Flux<Customer> getAllCustomers() {
    return customerService.listCustomers();
  }

  @Operation(summary = "Obtener cliente por ID", description = "Busca un cliente por su ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
      @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
      @ApiResponse(responseCode = "500", description = "Error interno del servidor")
  })
  @GetMapping("/{id}")
  public Mono<Customer> viewCustomer(@RequestHeader Map<String, String> headers, @PathVariable String id) {
    return customerService.getCustomer(id);
  }

  @Operation(summary = "Registrar un cliente", description = "Registra un nuevo cliente en el banco")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Cliente registrado correctamente"),
      @ApiResponse(responseCode = "400", description = "Cliente ya existe"),
      @ApiResponse(responseCode = "500", description = "Error interno del servidor")
  })
  @PostMapping
  public Mono<Customer> createCustomer(@Valid @RequestBody Customer customer) {
    logger.info("Intentando crear cliente con documento: {}", customer.getNumberDocument());
    return customerService.createCustomer(customer);
  }

  @Operation(summary = "Actualizar datos de un cliente", description = "Modifica datos de un cliente existente")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Cliente actualizado correctamente"),
    @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
    @ApiResponse(responseCode = "400", description = "Error en la solicitud"),
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
  })
  @PutMapping("/{id}")
  public Mono<Customer> updateCustomer(@PathVariable String id, @Valid @RequestBody Customer updatedCustomer) {
    logger.info("Intentando actualizar cliente con ID: {}", id);
    return customerService.updateCustomer(id, updatedCustomer);
  }

  @Operation(summary = "Eliminar un cliente", description = "Elimina un cliente de la base de datos.")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Cliente eliminado correctamente"),
    @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
    @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
  })
  @DeleteMapping("/{id}")
  public Mono<Void> deleteCustomer(@RequestHeader Map<String, String> headers, @PathVariable String id) {
    logger.info("Eliminando cliente con ID: {}", id);
    return customerService.deleteCustomer(id);
  }

  @Operation(summary = "Obtener resumen consolidado del cliente",
          description = "Muestra cuentas y créditos del cliente")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Resumen del cliente obtenido exitosamente"),
    @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
  })
  @GetMapping("/summary/{customerId}")
  public Mono<ResponseEntity<CustomerSummaryDTO>> getCustomerSummary(@PathVariable String customerId) {
    logger.info("Buscando productos de cliente con ID: {}", customerId);
    return customerService.getCustomerSummary(customerId)
            .map(ResponseEntity::ok);
  }
}