package com.assessment.demo.dao;

import com.assessment.demo.entities.Vacation;
import org.springframework.data.jpa.repository.JpaRepository;



public interface VacationRepository extends JpaRepository<Vacation, Long> {
}
