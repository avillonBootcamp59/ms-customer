package proyecto1.mscustomer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerSummaryDTO {
  private String customerId;
  private String name;
  private String documentNumber;
  private String type; // PERSONAL o EMPRESARIAL
  private String profile;
  private List<AccountDTO> accounts; // Lista de cuentas bancarias del cliente
  private List<CreditDTO> credits; // Lista de créditos del cliente
}
