package com.assessment.demo.dao;

import com.assessment.demo.entities.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;



@RepositoryRestResource(exported = false)
public interface CountryRepository extends JpaRepository<Country, Long> {
}
