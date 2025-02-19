package proyecto1.mscustomer.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import proyecto1.mscustomer.entity.Customer;
import proyecto1.mscustomer.repository.CustomerRepository;
import proyecto1.mscustomer.service.CustomerService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class CustomerServiceImpl implements CustomerService {

  private final CustomerRepository customerRepository;

  /**
   * Lista todos los clientes.
   */
  @Override
  public Flux<Customer> listCustomers() {
    return customerRepository.findAll();
  }

  /**
   * Obtiene un cliente por ID.
   */
  @Override
  public Mono<Customer> getCustomer(String id) {
    return customerRepository.findById(id)
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado")));
  }

  /**
   * Crea un nuevo cliente.
   */
  @Override
  public Mono<Customer> createCustomer(Customer customer) {
    return customerRepository.findByNumberDocument(customer.getNumberDocument())
            .flatMap(existing -> Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cliente ya registrado con este documento.")))
            .switchIfEmpty(customerRepository.save(customer)) // Asegura que el resultado siga siendo Mono<Customer>
            .cast(Customer.class); // Forzar la conversión a Mono<Customer>
  }


  /**
   * Actualiza los datos de un cliente existente.
   */
  @Override
  public Mono<Customer> updateCustomer(String id, Customer updatedCustomer) {
    return customerRepository.findById(id)
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado")))
            .flatMap(existingCustomer -> {
              existingCustomer.setName(updatedCustomer.getName());
              existingCustomer.setNumberDocument(updatedCustomer.getNumberDocument());
              existingCustomer.setType(updatedCustomer.getType());
              existingCustomer.setProfile(updatedCustomer.getProfile());
              return customerRepository.save(existingCustomer);
            });
  }


  /**
   * Elimina un cliente por ID.
   */
  @Override
  public Mono<Void> deleteCustomer(String id) {
    return customerRepository.findById(id)
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado")))
            .flatMap(existingCustomer -> customerRepository.deleteById(id));
  }

}
