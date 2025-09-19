package com.assessment.demo.controllers;

import com.assessment.demo.dao.VacationRepository;
import com.assessment.demo.entities.Vacation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/vacations")
public class VacationController {

    private final VacationRepository vacationRepository;

    @Autowired
    public VacationController(VacationRepository vacationRepository) {
        this.vacationRepository = vacationRepository;
    }

    // GET all vacations with _embedded structure for frontend compatibility
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getAllVacations() {
        List<Vacation> vacations = vacationRepository.findAll();
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> embedded = new HashMap<>();
        embedded.put("vacations", vacations);
        response.put("_embedded", embedded);

        HttpHeaders headers = new HttpHeaders();

        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_JSON).body(response);
    }

    // GET single vacation by ID
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Vacation> getVacationById(@PathVariable Long id) {
        return vacationRepository.findById(id)
                .map(vacation -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(vacation))
                .orElse(ResponseEntity.notFound().build());
    }

    // POST new vacation
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Vacation> createVacation(@RequestBody Vacation vacation) {
        try {
            Vacation savedVacation = vacationRepository.save(vacation);
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(savedVacation);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // PUT update existing vacation
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Vacation> updateVacation(@PathVariable Long id, @RequestBody Vacation vacationDetails) {
        return vacationRepository.findById(id)
                .map(vacation -> {
                    vacation.setVacation_title(vacationDetails.getVacation_title());
                    vacation.setDescription(vacationDetails.getDescription());
                    vacation.setTravel_price(vacationDetails.getTravel_price());
                    vacation.setImage_URL(vacationDetails.getImage_URL());
                    Vacation updatedVacation = vacationRepository.save(vacation);
                    return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(updatedVacation);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE vacation
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVacation(@PathVariable Long id) {
        return vacationRepository.findById(id)
                .map(vacation -> {
                    vacationRepository.delete(vacation);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
