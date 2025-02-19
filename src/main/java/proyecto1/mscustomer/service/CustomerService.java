package proyecto1.mscustomer.service;

import proyecto1.mscustomer.entity.Customer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerService {
  public Flux<Customer> listCustomers();
  public Mono<Customer> getCustomer(String id);
  public Mono<Void> deleteCustomer(String id);
  public Mono<Customer> createCustomer(Customer customer);
  public Mono<Customer> updateCustomer(String id, Customer updatedCustomer);
}
