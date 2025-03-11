package com.bank.pe.mscustomer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableWebFlux
@EnableReactiveMongoRepositories(basePackages = "com.bank.pe.mscustomer")
public class MsCustomerApplication {

  public static void main(String[] args) {
    SpringApplication.run(MsCustomerApplication.class, args);
  }

}
