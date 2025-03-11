package com.bank.pe.mscustomer.service;

import com.bank.pe.mscustomer.CustomersApiDelegate;
import com.bank.pe.mscustomer.client.AccountClient;
import com.bank.pe.mscustomer.client.CreditClient;
import com.bank.pe.mscustomer.dto.AccountDTO;
import com.bank.pe.mscustomer.dto.CreditDTO;
import com.bank.pe.mscustomer.dto.CustomerSummaryDTO;
import com.bank.pe.mscustomer.entity.CustomerEntity;
import com.bank.pe.mscustomer.mapper.CustomerConverterMapper;
import com.bank.pe.mscustomer.model.*;
import com.bank.pe.mscustomer.util.MetadataExtended;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.bank.pe.mscustomer.repository.CustomerRepository;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

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

  public CustomerServiceImpl(  CustomerRepository customerRepository,
                               CustomerConverterMapper customerConverterMapper,
                               CreditClient creditClient,
                               AccountClient accountClient) {
    this.customerRepository = customerRepository;
    this.customerConverterMapper = customerConverterMapper;
    this.creditClient = creditClient;
    this.accountClient = accountClient;
  }

  @Override
  public Optional<NativeWebRequest> getRequest() {
    return CustomersApiDelegate.super.getRequest();
  }

  @Override
  public Mono<ResponseEntity<CreateCustomerResponse>> create(
          Mono<CreateCustomerRequest> createCustomerRequest, ServerWebExchange exchange) {

    return createCustomerRequest.flatMap(request ->
      customerRepository.findByNumberDocument(request.getNumberDocument())
              .flatMap(existing -> Mono.error(
                      new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cliente ya registrado con este documento.")))
              .switchIfEmpty(Mono.defer(() -> {
                CustomerEntity customerEntity = new CustomerEntity(
                    request.getId(),
                    request.getName(),
                    request.getType(),
                    request.getNumberDocument(),
                    request.getEmail(),
                    request.getProfile()
                );
                return customerRepository.save(customerEntity);
              }))
              .cast(CustomerEntity.class)
              .map(savedCustomer -> {
                CreateCustomerDataResponse dataResponse = new CreateCustomerDataResponse();
                dataResponse.setId(savedCustomer.getId());

                CreateCustomerResponse response = new CreateCustomerResponse();
                response.setData(dataResponse);
                response.setMetadata(new MetadataExtended(200, "Operación exitosa"));

                return ResponseEntity.ok(response);

              })
    );
  }


  @Override
  public Mono<ResponseEntity<DeleteCustomerResponse>> delete(String id, ServerWebExchange exchange) {
    return customerRepository.findById(id)
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado")))
            .flatMap(existingCustomer ->
                customerRepository.delete(existingCustomer)
                    .then(Mono.defer(() -> {
                      DeleteCustomerResponse response = new DeleteCustomerResponse();
                      response.setMetadata(new MetadataExtended(200, "Cliente eliminado correctamente"));
                      return Mono.just(ResponseEntity.ok(response));
                    }))
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
              List<ListCustomerDataResponse>  customerDataList =
                      customerConverterMapper.toListCustomerDataResponseList(customers);

              ListCustomerResponse response = new ListCustomerResponse();
              response.setData(customerDataList);
              response.setMetadata(new MetadataExtended(200, "Operación exitosa"));

              return Mono.just(ResponseEntity.ok(response));
            });
  }


  @Override
  public Mono<ResponseEntity<ListCustomerResponse>> getById(String id, ServerWebExchange exchange) {
    return customerRepository.findById(id)
          .flatMap(customer -> {
            ListCustomerDataResponse customerData = customerConverterMapper.toListCustomerDataResponse(customer);
            if (customerData == null) {
              return Mono.error(new RuntimeException("Error en el mapeo del cliente"));
            }
            ListCustomerResponse response = new ListCustomerResponse();
            response.setData(List.of(customerData));
            response.setMetadata(new MetadataExtended(200, "Cliente encontrado"));

            return Mono.just(ResponseEntity.ok(response));
          })
          .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado")));


  }

  @Override
  public Mono<ResponseEntity<CustomerSummary>> summary(String customerId, ServerWebExchange exchange) {
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
  public Mono<ResponseEntity<UpdateCustomerResponse>> update(
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
                                UpdateCustomerDataResponse dataResponse = new UpdateCustomerDataResponse();
                                dataResponse.setId(updatedCustomer.getId());
                                UpdateCustomerResponse response = new UpdateCustomerResponse();
                                response.setData(dataResponse);
                                response.setMetadata(new MetadataExtended(200, "Cliente actualizado exitosamente"));
                                return ResponseEntity.ok(response);
                              });
                    })
            );

  }

}

