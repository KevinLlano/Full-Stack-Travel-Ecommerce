package com.assessment.demo.controllers;

import com.assessment.demo.dao.CustomerRepository;
import com.assessment.demo.dao.DivisionRepository;
import com.assessment.demo.entities.Customer;
import com.assessment.demo.entities.Division;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerRepository customerRepository;
    private final DivisionRepository divisionRepository;

    @Autowired
    public CustomerController(CustomerRepository customerRepository, DivisionRepository divisionRepository) {
        this.customerRepository = customerRepository;
        this.divisionRepository = divisionRepository;
    }

    // GET all customers with _embedded structure for frontend compatibility
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(ApiResponseHelper.wrapEmbedded("customers", customers));
    }

    // GET single customer by ID
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        return customerRepository.findById(id)
                .map(customer -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(customer))
                .orElse(ResponseEntity.notFound().build());
    }

    // POST new customer - using DTO to handle frontend format
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Customer> createCustomer(@RequestBody CustomerDTO customerDTO) {
        try {
            // Extract division ID from URL string
            Long divisionId = extractIdFromUrl(customerDTO.getDivision());

            // Find the division
            Optional<Division> divisionOpt = divisionRepository.findById(divisionId);
            if (!divisionOpt.isPresent()) {
                return ResponseEntity.badRequest().build();
            }

            // Create customer entity
            Customer customer = new Customer();
            customer.setFirstName(customerDTO.getFirstName());
            customer.setLastName(customerDTO.getLastName());
            customer.setAddress(customerDTO.getAddress());
            customer.setPostal_code(customerDTO.getPostal_code());
            customer.setPhone(customerDTO.getPhone());
            customer.setDivision(divisionOpt.get());

            Customer savedCustomer = customerRepository.save(customer);
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(savedCustomer);
        } catch (Exception e) {
            System.err.println("Error creating customer: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    // PUT update existing customer
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long id, @RequestBody CustomerDTO customerDTO) {
        return customerRepository.findById(id)
                .map(customer -> {
                    try {
                        Long divisionId = extractIdFromUrl(customerDTO.getDivision());
                        Optional<Division> divisionOpt = divisionRepository.findById(divisionId);

                        if (!divisionOpt.isPresent()) {
                            return ResponseEntity.badRequest().<Customer>build();
                        }

                        customer.setFirstName(customerDTO.getFirstName());
                        customer.setLastName(customerDTO.getLastName());
                        customer.setAddress(customerDTO.getAddress());
                        customer.setPostal_code(customerDTO.getPostal_code());
                        customer.setPhone(customerDTO.getPhone());
                        customer.setDivision(divisionOpt.get());

                        Customer updatedCustomer = customerRepository.save(customer);
                        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(updatedCustomer);
                    } catch (Exception e) {
                        return ResponseEntity.badRequest().<Customer>build();
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE customer
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable Long id) {
        try {
            return customerRepository.findById(id)
                .map(customer -> {
                    customerRepository.delete(customer);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Cannot delete customer: " + e.getMessage());
        }
    }    // GET division by customer ID (existing endpoint)
    @GetMapping("/{id}/division")
    public ResponseEntity<DivisionDto> getDivisionByCustomerId(@PathVariable Long id) {
        return customerRepository.findById(id)
                .map(Customer::getDivision)
                .map(div -> ResponseEntity.ok(new DivisionDto(div.getId(), div.getDivision_name(), div.getCountry_id())))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Helper method to extract ID from URL like "/api/divisions/101"
    private Long extractIdFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("URL cannot be null or empty");
        }

        // Handle both "/api/divisions/101" and "101" formats
        String[] parts = url.split("/");
        String idStr = parts[parts.length - 1];
        return Long.parseLong(idStr);
    }

    // DTO class to match frontend data format
    public static class CustomerDTO {
        private String firstName;
        private String lastName;
        private String address;
        private String postal_code;
        private String phone;
        private String division;
        private String country;

        // Getters and setters
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }

        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }

        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }

        public String getPostal_code() { return postal_code; }
        public void setPostal_code(String postal_code) { this.postal_code = postal_code; }

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }

        public String getDivision() { return division; }
        public void setDivision(String division) { this.division = division; }

        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }
    }

    // simple DTO to avoid serializing lazy relations
    public static class DivisionDto {
        private Long id;
        private String division;
        private Long countryId;

        public DivisionDto(Long id, String division, Long countryId) {
            this.id = id;
            this.division = division;
            this.countryId = countryId;
        }

        public Long getId() { return id; }
        public String getDivision() { return division; }
        public Long getCountryId() { return countryId; }
    }
}
