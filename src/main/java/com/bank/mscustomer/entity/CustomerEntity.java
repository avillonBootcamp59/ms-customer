package com.bank.mscustomer.entity;

import lombok.*;
import org.bson.codecs.pojo.annotations.BsonId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "customers")
@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class CustomerEntity {
  @BsonId
  private String id;
  private String name;
  private String type; // Personal o Empresarial

  @Indexed(unique = true)
  private String numberDocument; // DNI o RUC
  private String email;
  private String profile; // VIP, PYME



}
