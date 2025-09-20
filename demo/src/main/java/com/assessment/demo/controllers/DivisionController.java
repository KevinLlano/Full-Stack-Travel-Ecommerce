package com.assessment.demo.controllers;

import com.assessment.demo.dao.DivisionRepository;
import com.assessment.demo.entities.Division;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/divisions")
public class DivisionController {

    private final DivisionRepository divisionRepository;

    @Autowired
    public DivisionController(DivisionRepository divisionRepository) {
        this.divisionRepository = divisionRepository;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getAllDivisions() {
        List<Division> divisions = divisionRepository.findAll();
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(ApiResponseHelper.wrapEmbedded("divisions", divisions));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Division> getDivisionById(@PathVariable Long id) {
        return divisionRepository.findById(id)
                .map(d -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(d))
                .orElse(ResponseEntity.notFound().build());
    }
}
