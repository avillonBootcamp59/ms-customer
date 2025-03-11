package com.bank.pe.mscustomer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditDTO {
  private String id;
  private String customerId;
  private String creditType;
  private Double creditLimit;
  private Double currentDebt;
  private Double availableLimit;

  public CreditDTO(String id,
                   String customerId,
                   String creditType,
                   double creditLimit,
                   double currentDebt,
                   double availableLimit) {
  }
}
