package com.bank.mscustomer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication
@EnableReactiveMongoRepositories(basePackages = "com.bank.mscustomer.repository")
public class MsCustomerApplication {

  public static void main(String[] args) {
    SpringApplication.run(MsCustomerApplication.class, args);
  }

}
