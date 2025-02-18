package proyecto1.mscustomer.entity;

import lombok.Data;
import org.bson.codecs.pojo.annotations.BsonId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "customers")
public class Customer {
    @BsonId
    private String id;
    private String name;
    private String type; // Personal o Empresarial

    @Indexed(unique = true)
    private String numberDocument; // DNI o RUC

    private String email;
    private String profile; // VIP, PYME
}
