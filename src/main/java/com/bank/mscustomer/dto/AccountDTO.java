package com.bank.mscustomer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTO {
  private String id;
  private String number;
  private String type;
  private String customerId;
  private Double balance;
  private int transactionLimit;
  private Double commissionFee;

  public AccountDTO(String id,
                    String number,
                    String ahorro,
                    String customerId,
                    double v,
                    int i,
                    double v1) {
  }
}
