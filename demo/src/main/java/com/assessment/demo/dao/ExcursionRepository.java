package com.assessment.demo.dao;

import com.assessment.demo.entities.Excursion;
import org.springframework.data.jpa.repository.JpaRepository;



public interface ExcursionRepository extends JpaRepository<Excursion, Long> {
}
