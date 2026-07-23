/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.welltrack.welltrack_bi.controller;

/**
 *
 * @author marianarodriguesoliveira
 */


import com.welltrack.welltrack_bi.model.Practitioner;
import com.welltrack.welltrack_bi.repository.PractitionerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * PractitionerController — REST API endpoints for managing practitioners.
 * Returns proper HTTP status codes as per REST API lecture.
 */
@RestController
@RequestMapping("/api/practitioners")
public class PractitionerController {

    @Autowired
    private PractitionerRepository practitionerRepository;

    /**
     * GET /api/practitioners — returns all active practitioners.
     * HTTP 200 OK on success.
     * @return 
     */
    @GetMapping
    public ResponseEntity<List<Practitioner>> getAllPractitioners() {
        List<Practitioner> practitioners = practitionerRepository.findByActiveTrue();
        return ResponseEntity.ok(practitioners); // 200 OK
    }

    /**
     * GET /api/practitioners/{id} — returns a single practitioner by ID.
     * HTTP 200 OK if found, 404 Not Found if not.
     * @param id
     * @return 
     */
    @GetMapping("/{id}")
    public ResponseEntity<Practitioner> getPractitionerById(@PathVariable Integer id) {
        return practitionerRepository.findById(id)
                .map(p -> ResponseEntity.ok(p))
                .orElse(ResponseEntity.notFound().build()); // 404 Not Found
    }

    /**
     * POST /api/practitioners — creates a new practitioner.
     * HTTP 201 Created on success.
     * @param practitioner
     * @return 
     */
    @PostMapping
    public ResponseEntity<Practitioner> createPractitioner(
            @RequestBody Practitioner practitioner) {
        practitioner.setActive(true);
        Practitioner saved = practitionerRepository.save(practitioner);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved); // 201 Created
    }

    /**
     * PUT /API/practitioners/{id} — updates an existing practitioner.
     * HTTP 200 OK if updated, 404 Not Found if not found.
     * @param id
     * @param updated
     * @return 
     */
    @PutMapping("/{id}")
    public ResponseEntity<Practitioner> updatePractitioner(
            @PathVariable Integer id,
            @RequestBody Practitioner updated) {
        return practitionerRepository.findById(id)
                .map(p -> {
                    p.setFullName(updated.getFullName());
                    p.setSpecialty(updated.getSpecialty());
                    return ResponseEntity.ok(practitionerRepository.save(p)); // 200 OK
                })
                .orElse(ResponseEntity.notFound().build()); // 404 Not Found
    }

    /**
     * DELETE /api/practitioners/{id} — soft deletes a practitioner.
     * HTTP 200 OK if deactivated, 404 if not found.
     * @param id
     * @return 
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePractitioner(@PathVariable Integer id) {
        return practitionerRepository.findById(id)
                .map(p -> {
                    p.setActive(false);
                    practitionerRepository.save(p);
                    return ResponseEntity.ok("Practitioner deactivated successfully"); // 200 OK
                })
                .orElse(ResponseEntity.notFound().build()); // 404 Not Found
    }
}
