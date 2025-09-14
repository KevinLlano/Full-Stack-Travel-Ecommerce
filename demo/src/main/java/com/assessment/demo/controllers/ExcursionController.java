package com.assessment.demo.controllers;

import com.assessment.demo.dao.ExcursionRepository;
import com.assessment.demo.dao.VacationRepository;
import com.assessment.demo.entities.Excursion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/excursions")
public class ExcursionController {

    private final ExcursionRepository excursionRepository;
    private final VacationRepository vacationRepository;

    @Autowired
    public ExcursionController(ExcursionRepository excursionRepository, VacationRepository vacationRepository) {
        this.excursionRepository = excursionRepository;
        this.vacationRepository = vacationRepository;
    }

    // GET all excursions with _embedded structure for frontend compatibility
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getAllExcursions() {
        List<Excursion> excursions = excursionRepository.findAll();
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> embedded = new HashMap<>();
        embedded.put("excursions", excursions);
        response.put("_embedded", embedded);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(response);
    }

    // GET single excursion by ID
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Excursion> getExcursionById(@PathVariable Long id) {
        return excursionRepository.findById(id)
                .map(excursion -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(excursion))
                .orElse(ResponseEntity.notFound().build());
    }

    // GET excursions by vacation ID - important for frontend to show excursions for a specific vacation
    @GetMapping(value = "/search/findByVacationId", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getExcursionsByVacationId(@RequestParam Long vacationId) {
        List<Excursion> excursions = excursionRepository.findAll()
                .stream()
                .filter(excursion -> excursion.getVacation() != null && excursion.getVacation().getId().equals(vacationId))
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        Map<String, Object> embedded = new HashMap<>();
        embedded.put("excursions", excursions);
        response.put("_embedded", embedded);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(response);
    }

    // Alternative endpoint for getting excursions by vacation (REST style)
    @GetMapping(value = "/vacation/{vacationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getExcursionsByVacation(@PathVariable Long vacationId) {
        List<Excursion> excursions = excursionRepository.findAll()
                .stream()
                .filter(excursion -> excursion.getVacation() != null && excursion.getVacation().getId().equals(vacationId))
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        Map<String, Object> embedded = new HashMap<>();
        embedded.put("excursions", excursions);
        response.put("_embedded", embedded);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(response);
    }

    // POST new excursion
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Excursion> createExcursion(@RequestBody Excursion excursion) {
        try {
            Excursion savedExcursion = excursionRepository.save(excursion);
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(savedExcursion);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // PUT update existing excursion
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Excursion> updateExcursion(@PathVariable Long id, @RequestBody Excursion excursionDetails) {
        return excursionRepository.findById(id)
                .map(excursion -> {
                    excursion.setExcursion_title(excursionDetails.getExcursion_title());
                    excursion.setExcursion_price(excursionDetails.getExcursion_price());
                    excursion.setImage_URL(excursionDetails.getImage_URL());
                    excursion.setVacation(excursionDetails.getVacation());
                    Excursion updatedExcursion = excursionRepository.save(excursion);
                    return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(updatedExcursion);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE excursion
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExcursion(@PathVariable Long id) {
        return excursionRepository.findById(id)
                .map(excursion -> {
                    excursionRepository.delete(excursion);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
