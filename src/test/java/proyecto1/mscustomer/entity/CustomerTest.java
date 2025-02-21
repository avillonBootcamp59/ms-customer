package proyecto1.mscustomer.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import proyecto1.mscustomer.entity.Customer;
import static org.junit.jupiter.api.Assertions.*;

public class CustomerTest {

    @Test
    void testCustomerSerialization() throws Exception {
        Customer customer = new Customer();
        customer.setName("Vanesa Juarez");
        customer.setType("Personal");
        customer.setNumberDocument("12093345");
        customer.setEmail("vj@gmail.com");
        customer.setProfile("VIP");
        customer.setId("1");
        ObjectMapper objectMapper = new ObjectMapper();

        String json = objectMapper.writeValueAsString(customer);
        Customer deserialized = objectMapper.readValue(json, Customer.class);

        assertEquals(customer, deserialized);
    }
    @Test
    void testEquals() {
        Customer customer1 = new Customer("1", "Maria Casas", "Personal", "12345678", "Maria@email.com", "Normal");
        Customer customer2 = new Customer("1", "Maria Casas", "Personal", "12345678", "Maria@email.com", "Normal");
        Customer customer3 = new Customer("2", "Fiorella Casas", "Personal", "87654321", "Fiorella@email.com", "VIP");

        assertEquals(customer1, customer2);  // Deben ser iguales
        assertNotEquals(customer1, customer3);  // Deben ser diferentes
        assertNotEquals(customer1, new Object());  // Diferente de otro tipo
        assertNotEquals(customer1, null);  // Diferente de null
    }

    @Test
    void testHashCode() {
        Customer customer1 = new Customer("1", "Maria Casas", "Personal", "12345678", "Maria@email.com", "Normal");
        Customer customer2 = new Customer("1", "Maria Casas", "Personal", "12345678", "Maria@email.com", "Normal");
        Customer customer3 = new Customer("2", "Fiorella Casas", "Personal", "87654321", "Fiorella@email.com", "VIP");

        assertEquals(customer1.hashCode(), customer2.hashCode());  // HashCode debe ser el mismo
        assertNotEquals(customer1.hashCode(), customer3.hashCode());  // HashCode debe ser diferente
    }

}
