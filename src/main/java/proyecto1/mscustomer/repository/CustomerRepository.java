package proyecto1.mscustomer.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import proyecto1.mscustomer.entity.Customer;

@Repository
public interface CustomerRepository extends ReactiveMongoRepository<Customer, String> {
}
