package org.example.nirsshop.repository;

import org.example.nirsshop.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    List<Customer> findByFullNameContainingIgnoreCase(String fullName);

}
