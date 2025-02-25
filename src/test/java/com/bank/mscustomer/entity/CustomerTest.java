package com.bank.mscustomer.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerTest {

    @Test
    void testCustomerSerialization() throws Exception {
        CustomerEntity customer = new CustomerEntity();
        customer.setName("Vanesa Juarez");
        customer.setType("Personal");
        customer.setNumberDocument("12093345");
        customer.setEmail("vj@gmail.com");
        customer.setProfile("VIP");
        customer.setId("1");
        ObjectMapper objectMapper = new ObjectMapper();

        String json = objectMapper.writeValueAsString(customer);
        CustomerEntity deserialized = objectMapper.readValue(json, CustomerEntity.class);

        assertEquals(customer, deserialized);
    }
    @Test
    void testEquals() {
        CustomerEntity customer1 = new CustomerEntity("1", "Maria Casas", "Personal", "12345678", "Maria@email.com", "Normal");
        CustomerEntity customer2 = new CustomerEntity("1", "Maria Casas", "Personal", "12345678", "Maria@email.com", "Normal");
        CustomerEntity customer3 = new CustomerEntity("2", "Fiorella Casas", "Personal", "87654321", "Fiorella@email.com", "VIP");

        assertEquals(customer1, customer2);  // Deben ser iguales
        assertNotEquals(customer1, customer3);  // Deben ser diferentes
        assertNotEquals(customer1, new Object());  // Diferente de otro tipo
        assertNotEquals(customer1, null);  // Diferente de null
    }

    @Test
    void testHashCode() {
        CustomerEntity customer1 = new CustomerEntity("1", "Maria Casas", "Personal", "12345678", "Maria@email.com", "Normal");
        CustomerEntity customer2 = new CustomerEntity("1", "Maria Casas", "Personal", "12345678", "Maria@email.com", "Normal");
        CustomerEntity customer3 = new CustomerEntity("2", "Fiorella Casas", "Personal", "87654321", "Fiorella@email.com", "VIP");

        assertEquals(customer1.hashCode(), customer2.hashCode());  // HashCode debe ser el mismo
        assertNotEquals(customer1.hashCode(), customer3.hashCode());  // HashCode debe ser diferente
    }

}
