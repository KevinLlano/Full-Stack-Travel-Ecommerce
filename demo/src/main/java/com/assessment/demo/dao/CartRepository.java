package com.assessment.demo.dao;

import com.assessment.demo.entities.Cart;
import org.springframework.data.jpa.repository.JpaRepository;



public interface CartRepository extends JpaRepository<Cart, Long> {
}
