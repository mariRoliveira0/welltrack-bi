/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.welltrack.welltrack_bi.controller;

/**
 *
 * @author marianarodriguesoliveira
 */


import com.welltrack.welltrack_bi.model.Client;
import com.welltrack.welltrack_bi.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * ClientController — REST API endpoints for managing clients.
 */
@RestController
@RequestMapping("/api/clients")
public class ClientController {

    @Autowired
    private ClientRepository clientRepository;

    /**
     * GET /API/clients returns all active clients.
     * HTTP 200 OK on success.
     */
    @GetMapping
    public ResponseEntity<List<Client>> getAllClients() {
        List<Client> clients = clientRepository.findByActiveTrue();
        return ResponseEntity.ok(clients); // 200 OK
    }

    /**
     * GET /API/clients/{id} — returns a single client by ID.
     * HTTP 200 OK if found, 404 Not Found if not.
     * @param id
     * @return 
     */
    @GetMapping("/{id}")
    public ResponseEntity<Client> getClientById(@PathVariable Integer id) {
        return clientRepository.findById(id)
                .map(client -> ResponseEntity.ok(client))           // 200 OK
                .orElse(ResponseEntity.notFound().build());         // 404 Not Found
    }

    /**
     * POST /API/clients — creates a new client.
     * HTTP 201 Created on success.
     * @param client
     * @return 
     */
    @PostMapping
    public ResponseEntity<Client> createClient(@RequestBody Client client) {
        client.setActive(true);
        Client saved = clientRepository.save(client);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved); // 201 Created
    }

    /**
     * PUT /API/clients/{id} — updates an existing client.
     * HTTP 200 OK if updated, 404 Not Found if client doesn't exist.
     * @param id
     * @param updated
     * @return 
     */
    @PutMapping("/{id}")
    public ResponseEntity<Client> updateClient(@PathVariable Integer id,
                                               @RequestBody Client updated) {
        return clientRepository.findById(id)
                .map(client -> {
                    client.setFullName(updated.getFullName());
                    client.setEmail(updated.getEmail());
                    client.setPhone(updated.getPhone());
                    client.setDateOfBirth(updated.getDateOfBirth());
                    client.setNotes(updated.getNotes());
                    return ResponseEntity.ok(clientRepository.save(client)); // 200 OK
                })
                .orElse(ResponseEntity.notFound().build());                  // 404 Not Found
    }

    /**
     * DELETE /api/clients/{id} — soft deletes a client (sets active = false).
     * We never permanently delete data — just deactivate.
     * HTTP 200 OK if deleted, 404 Not Found if client doesn't exist.
     * @param id
     * @return 
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteClient(@PathVariable Integer id) {
        return clientRepository.findById(id)
                .map(client -> {
                    client.setActive(false);
                    clientRepository.save(client);
                    return ResponseEntity.ok("Client deactivated successfully"); // 200 OK
                })
                .orElse(ResponseEntity.notFound().build());                      // 404 Not Found
    }
}