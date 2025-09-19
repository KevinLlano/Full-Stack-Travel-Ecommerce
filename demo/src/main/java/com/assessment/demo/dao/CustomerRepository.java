package com.assessment.demo.dao;

import com.assessment.demo.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;



public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
