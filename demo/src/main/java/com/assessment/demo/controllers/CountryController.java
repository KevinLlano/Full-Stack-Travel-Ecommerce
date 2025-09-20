package com.assessment.demo.controllers;

import com.assessment.demo.dao.CountryRepository;
import com.assessment.demo.entities.Country;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/countries")
public class CountryController {

    private final CountryRepository countryRepository;

    @Autowired
    public CountryController(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getAllCountries() {
        List<Country> countries = countryRepository.findAll();
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(ApiResponseHelper.wrapEmbedded("countries", countries));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Country> getCountryById(@PathVariable Long id) {
        return countryRepository.findById(id)
                .map(c -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(c))
                .orElse(ResponseEntity.notFound().build());
    }
}
