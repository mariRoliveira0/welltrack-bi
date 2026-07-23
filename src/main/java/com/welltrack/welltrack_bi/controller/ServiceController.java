/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.welltrack.welltrack_bi.controller;

/**
 *
 * @author marianarodriguesoliveira
 */


import com.welltrack.welltrack_bi.model.Service;
import com.welltrack.welltrack_bi.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * ServiceController — REST API endpoints for managing
 * the services offered by the business (e.g. Pilates Class,
 * Chiro Appointment). Returns proper HTTP status codes
 * as per REST API lecture.
 */
@RestController
@RequestMapping("/api/services")
public class ServiceController {

    @Autowired
    private ServiceRepository serviceRepository;

    /**
     * GET /API/services — returns all active services.
     * HTTP 200 OK on success.
     * @return 
     */
    @GetMapping
    public ResponseEntity<List<Service>> getAllServices() {
        List<Service> services = serviceRepository.findByActiveTrue();
        return ResponseEntity.ok(services); // 200 OK
    }

    /**
     * GET /api/services/{id} — returns a single service by ID.
     * HTTP 200 OK if found, 404 Not Found if not.
     * @param id
     * @return 
     */
    @GetMapping("/{id}")
    public ResponseEntity<Service> getServiceById(@PathVariable Integer id) {
        return serviceRepository.findById(id)
                .map(s -> ResponseEntity.ok(s))
                .orElse(ResponseEntity.notFound().build()); // 404 Not Found
    }

    /**
     * GET /API/services/type/{type} — returns services filtered
     * by type (GROUP or PRIVATE).
     * HTTP 200 OK on success.
     * @param type
     * @return 
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Service>> getServicesByType(
            @PathVariable Service.ServiceType type) {
        List<Service> services = serviceRepository.findByServiceType(type);
        return ResponseEntity.ok(services); // 200 OK
    }

    /**
     * POST /API/services — creates a new service.
     * HTTP 201 Created on success.
     * @param service
     * @return 
     */
    @PostMapping
    public ResponseEntity<Service> createService(@RequestBody Service service) {
        service.setActive(true);
        Service saved = serviceRepository.save(service);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved); // 201 Created
    }

    /**
     * PUT /API/services/{id} — updates an existing service.
     * HTTP 200 OK if updated, 404 Not Found if not found.
     * @param id
     * @param updated
     * @return 
     */
    @PutMapping("/{id}")
    public ResponseEntity<Service> updateService(@PathVariable Integer id,
                                                  @RequestBody Service updated) {
        return serviceRepository.findById(id)
                .map(s -> {
                    s.setName(updated.getName());
                    s.setDescription(updated.getDescription());
                    s.setPrice(updated.getPrice());
                    s.setDurationMin(updated.getDurationMin());
                    s.setServiceType(updated.getServiceType());
                    return ResponseEntity.ok(serviceRepository.save(s)); // 200 OK
                })
                .orElse(ResponseEntity.notFound().build()); // 404 Not Found
    }

    /**
     * DELETE /api/services/{id} — soft deletes a service.
     * HTTP 200 OK if deactivated, 404 if not found.
     * @param id
     * @return 
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteService(@PathVariable Integer id) {
        return serviceRepository.findById(id)
                .map(s -> {
                    s.setActive(false);
                    serviceRepository.save(s);
                    return ResponseEntity.ok("Service deactivated successfully"); // 200 OK
                })
                .orElse(ResponseEntity.notFound().build()); // 404 Not Found
    }
}