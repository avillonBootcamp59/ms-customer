package com.bank.mscustomer.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import com.bank.mscustomer.entity.CustomerEntity;
import reactor.core.publisher.Mono;

@Repository
public interface CustomerRepository extends ReactiveMongoRepository<CustomerEntity, String> {
  Mono<CustomerEntity> findByNumberDocument(String numberDocument);
}
