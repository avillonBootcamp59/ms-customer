package com.bank.mscustomer.service;

import com.bank.mscustomer.CustomersApiDelegate;
import com.bank.mscustomer.client.AccountClient;
import com.bank.mscustomer.client.CreditClient;
import com.bank.mscustomer.dto.AccountDTO;
import com.bank.mscustomer.dto.CreditDTO;
import com.bank.mscustomer.dto.CustomerSummaryDTO;
import com.bank.mscustomer.entity.CustomerEntity;
import com.bank.mscustomer.mapper.CustomerConverterMapper;
import com.bank.mscustomer.model.*;
import com.bank.mscustomer.util.MetadataExtended;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.bank.mscustomer.repository.CustomerRepository;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomersApiDelegate {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  CustomerRepository customerRepository;

  @Autowired
  CustomerConverterMapper customerConverterMapper;

  @Autowired
  CreditClient creditClient;

  @Autowired
  AccountClient accountClient;

  public CustomerServiceImpl(  CustomerRepository customerRepository) {
    this.customerRepository = customerRepository;
  }

  @Override
  public Optional<NativeWebRequest> getRequest() {
    return CustomersApiDelegate.super.getRequest();
  }

  @Override
  public Mono<ResponseEntity<CreateCustomerResponse>> createCustomer(
          Mono<CreateCustomerRequest> createCustomerRequest, ServerWebExchange exchange) {
    return createCustomerRequest.flatMap(request ->
          customerRepository.findByNumberDocument(request.getNumberDocument())
                  .flatMap(existing -> Mono.error(new ResponseStatusException(
                          HttpStatus.BAD_REQUEST, "Cliente ya registrado con este documento.")))
                  .switchIfEmpty(Mono.defer(() -> {
                    CustomerEntity customer = new CustomerEntity(
                              request.getId(),
                              request.getName(),
                              request.getType(),
                              request.getNumberDocument(),
                              request.getEmail(),
                              request.getProfile()
                      );
                    return customerRepository.save(customer);
                  }))
                  .map(savedCustomer -> {
                    CreateCustomerDataResponse dataResponse = new CreateCustomerDataResponse();
                    dataResponse.setId(dataResponse.getId());

                    CreateCustomerResponse response = new CreateCustomerResponse();
                    response.setData(dataResponse);
                    response.setMetadata(new MetadataExtended(200, "Cliente creado exitosamente"));

                    return ResponseEntity.ok(response);
                  })
      );


  }

  @Override
  public Mono<ResponseEntity<DeleteCustomerResponse>> deleteCustomer(String id, ServerWebExchange exchange) {
    return customerRepository.findById(id)
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado")))
            .flatMap(existingCustomer -> customerRepository.delete(existingCustomer)
                    .then(Mono.just(ResponseEntity.ok(new DeleteCustomerResponse()
                            .metadata(new MetadataExtended(200, "Cliente eliminado correctamente")))))
            );

  }

  @Override
  public Mono<ResponseEntity<ListCustomerResponse>> getAll(ServerWebExchange exchange) {
    return customerRepository.findAll()
            .collectList()
            .flatMap(customers -> {
              if (customers.isEmpty()) {
                return Mono.error(new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No hay clientes registrados."));
              }

              List<ListCustomerDataResponse> customerDataList =
                      customerConverterMapper.toListCustomerDataResponseList(customers);

              ListCustomerResponse response = new ListCustomerResponse();
              response.setData(customerDataList);
              response.setMetadata(new MetadataExtended(200, "Operación exitosa"));


              return Mono.just(ResponseEntity.ok(response));
            });
  }


  @Override
  public Mono<ResponseEntity<ListCustomerResponse>> getCustomerById(String id, ServerWebExchange exchange) {
    return customerRepository.findById(id)
            .map(customer -> {
              ListCustomerResponse response = new ListCustomerResponse();
              response.setData(List.of(customerConverterMapper.toListCustomerDataResponse(customer)));
              response.setMetadata(new MetadataExtended(200, "Cliente encontrado"));

              return ResponseEntity.ok(response);
            })
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado")));

  }

  @Override
  public Mono<ResponseEntity<CustomerSummary>> getCustomerSummary(String customerId, ServerWebExchange exchange) {
    return customerRepository.findById(customerId)
          .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado")))
          .flatMap(customer -> {
            Mono<List<AccountDTO>> accounts = accountClient.getAccountsByCustomer(customerId)
                      .collectList()
                      .defaultIfEmpty(List.of());

            Mono<List<CreditDTO>> credits = creditClient.getCreditProductsByCustomer(customerId)
                      .collectList()
                      .defaultIfEmpty(List.of());

            return Mono.zip(accounts, credits)
                      .map(tuple -> {
                        CustomerSummaryDTO summaryDTO = new CustomerSummaryDTO();
                        summaryDTO.setCustomerId(customer.getId());
                        summaryDTO.setName(customer.getName());
                        summaryDTO.setDocumentNumber(customer.getNumberDocument());
                        summaryDTO.setType(customer.getType());
                        summaryDTO.setProfile(customer.getProfile());
                        summaryDTO.setAccounts(tuple.getT1());
                        summaryDTO.setCredits(tuple.getT2());
                        CustomerSummary summary = customerConverterMapper.toCustomerSummary(summaryDTO);
                        return ResponseEntity.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(summary);
                      });
          });

  }


  @Override
  public Mono<ResponseEntity<UpdateCustomerResponse>> updateCustomer(
          Mono<UpdateCustomerRequest> updateCustomerRequest, ServerWebExchange exchange) {
    return updateCustomerRequest
            .flatMap(request -> customerRepository.findById(request.getId())
                    .switchIfEmpty(Mono.error(new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Cliente no encontrado")))
                    .flatMap(existingCustomer -> {
                      existingCustomer.setName(request.getName());
                      existingCustomer.setNumberDocument(request.getNumberDocument());
                      existingCustomer.setType(request.getType());
                      existingCustomer.setProfile(request.getProfile());

                      return customerRepository.save(existingCustomer)
                              .map(updatedCustomer -> {
                                UpdateCustomerResponse response = new UpdateCustomerResponse();
                                response.setData(customerConverterMapper.toUpdateCustomerDataResponse(updatedCustomer));
                                response.setMetadata(new MetadataExtended(200, "Cliente actualizado exitosamente"));
                                return ResponseEntity.ok(response);
                              });
                    })
            );

  }



  /**
  * Obtiene un cliente por ID.
  */
  /*@Override
  public Mono<Customer> getCustomer(String id) {
    return customerRepository.findById(id)
    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado")));
  }*/

  /**
  * Crea un nuevo cliente.
  */
 /* @Override
  public Mono<Customer> createCustomer(Customer customer) {
    return customerRepository.findByNumberDocument(customer.getNumberDocument())
    .flatMap(existing -> Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
        "Cliente ya registrado con este documento.")))
    .switchIfEmpty(Mono.defer(() -> customerRepository.save(customer)))
    .cast(Customer.class);
  }
*/

  /**
  * Actualiza los datos de un cliente existente.
  */
  /*@Override
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
  }*/


  /**
  * Elimina un cliente por ID.
  */
  /*@Override
  public Mono<Void> deleteCustomer(String id) {
    return customerRepository.findById(id)
    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado")))
    .flatMap(existingCustomer -> customerRepository.deleteById(id));
  }

  @Override
  public Mono<CustomerSummaryDTO> getCustomerSummary(String customerId) {
    return customerRepository.findById(customerId)
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado")))
            .flatMap(customer -> {
              Mono<List<AccountDTO>> accounts = accountClient.getAccountsByCustomer(
                      customerId).collectList().defaultIfEmpty(List.of()); ;
              Mono<List<CreditDTO>> credits = creditClient.getCreditProductsByCustomer(
                      customerId).collectList().defaultIfEmpty(List.of()); ;

              return Mono.zip(accounts, credits)
                      .map(tuple -> new CustomerSummaryDTO(
                              customer.getId(),
                              customer.getName(),
                              customer.getNumberDocument(),
                              customer.getType(),
                              customer.getProfile(),
                              tuple.getT1(), // Lista de cuentas
                              tuple.getT2()  // Lista de créditos
                      ));
            });
  }
*/
}
