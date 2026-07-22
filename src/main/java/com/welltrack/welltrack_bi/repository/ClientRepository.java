/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.welltrack.welltrack_bi.repository;

/**
 *
 * @author marianarodriguesoliveira
 */

import com.welltrack.welltrack_bi.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Integer> {

    // Find all active clients
    List<Client> findByActiveTrue();

    // Find client by email
    Client findByEmail(String email);
}