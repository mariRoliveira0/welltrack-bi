/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.welltrack.welltrack_bi.repository;

/**
 *
 * @author marianarodriguesoliveira
 */


import com.welltrack.welltrack_bi.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<Session, Integer> {

    // Find sessions by date
    List<Session> findBySessionDate(LocalDate sessionDate);

    // Find sessions by practitioner
    List<Session> findByPractitionerId(Integer practitionerId);

    // Find sessions between two dates
    List<Session> findBySessionDateBetween(LocalDate start, LocalDate end);
}