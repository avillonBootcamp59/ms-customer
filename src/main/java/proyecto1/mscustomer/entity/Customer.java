package proyecto1.mscustomer.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "customers")
public class Customer {
    @Id
    private String id;
    private String name;
    private String type; // Personal o Empresarial
    private String numberDocument; // DNI ó Ruc
    private String email;
}
