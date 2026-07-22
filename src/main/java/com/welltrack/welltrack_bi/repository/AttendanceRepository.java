/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.welltrack.welltrack_bi.repository;

/**
 *
 * @author marianarodriguesoliveira
 */


import com.welltrack.welltrack_bi.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {

    // Find all attendance records for a session
    List<Attendance> findBySessionId(Integer sessionId);

    // Find all sessions a client attended
    List<Attendance> findByClientId(Integer clientId);

    // Count how many clients attended a session
    long countBySessionId(Integer sessionId);
}