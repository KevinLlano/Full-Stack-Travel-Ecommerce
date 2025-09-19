package com.assessment.demo.dao;

import com.assessment.demo.entities.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;



public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
    