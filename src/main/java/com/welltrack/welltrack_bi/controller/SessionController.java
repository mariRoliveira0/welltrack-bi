/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.welltrack.welltrack_bi.controller;

/**
 *
 * @author marianarodriguesoliveira
 */


import com.welltrack.welltrack_bi.model.Session;
import com.welltrack.welltrack_bi.model.Attendance;
import com.welltrack.welltrack_bi.repository.SessionRepository;
import com.welltrack.welltrack_bi.repository.AttendanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

/**
 * SessionController — REST API endpoints for managing sessions
 * (group classes and private appointments) and attendance.
 * Returns proper HTTP status codes as per REST API lecture.
 */
@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    /**
     * GET /api/sessions — returns all sessions.
     * HTTP 200 OK on success.
     * @return 
     */
    @GetMapping
    public ResponseEntity<List<Session>> getAllSessions() {
        List<Session> sessions = sessionRepository.findAll();
        return ResponseEntity.ok(sessions); // 200 OK
    }

    /**
     * GET /API/sessions/{id} — returns a single session by ID.
     * HTTP 200 OK if found, 404 Not Found if not.
     * @param id
     * @return 
     */
    @GetMapping("/{id}")
    public ResponseEntity<Session> getSessionById(@PathVariable Integer id) {
        return sessionRepository.findById(id)
                .map(s -> ResponseEntity.ok(s))
                .orElse(ResponseEntity.notFound().build()); // 404 Not Found
    }

    /**
     * GET /API/sessions/date/{date} — returns sessions on a specific date.
     * Date format: yyyy-MM-dd (e.g. 2026-07-21)
     * HTTP 200 OK on success.
     * @param date
     * @return 
     */
    @GetMapping("/date/{date}")
    public ResponseEntity<List<Session>> getSessionsByDate(
            @PathVariable String date) {
        LocalDate localDate = LocalDate.parse(date);
        List<Session> sessions = sessionRepository.findBySessionDate(localDate);
        return ResponseEntity.ok(sessions); // 200 OK
    }

    /**
     * GET /API/sessions/practitioner/{id} — returns all sessions
     * for a specific practitioner.
     * HTTP 200 OK on success.
     * @param id
     * @return 
     */
    @GetMapping("/practitioner/{id}")
    public ResponseEntity<List<Session>> getSessionsByPractitioner(
            @PathVariable Integer id) {
        List<Session> sessions = sessionRepository.findByPractitionerId(id);
        return ResponseEntity.ok(sessions); // 200 OK
    }

    /**
     * POST /API/sessions — creates a new session.
     * HTTP 201 Created on success.
     * @param session
     * @return 
     */
    @PostMapping
    public ResponseEntity<Session> createSession(@RequestBody Session session) {
        Session saved = sessionRepository.save(session);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved); // 201 Created
    }

    /**
     * PUT /API/sessions/{id} — updates an existing session.
     * HTTP 200 OK if updated, 404 Not Found if not found.
     * @param id
     * @param updated
     * @return 
     */
    @PutMapping("/{id}")
    public ResponseEntity<Session> updateSession(@PathVariable Integer id,
                                                  @RequestBody Session updated) {
        return sessionRepository.findById(id)
                .map(s -> {
                    s.setSessionDate(updated.getSessionDate());
                    s.setStartTime(updated.getStartTime());
                    s.setCapacity(updated.getCapacity());
                    s.setNotes(updated.getNotes());
                    s.setService(updated.getService());
                    s.setPractitioner(updated.getPractitioner());
                    return ResponseEntity.ok(sessionRepository.save(s)); // 200 OK
                })
                .orElse(ResponseEntity.notFound().build()); // 404 Not Found
    }

    /**
     * DELETE /API/sessions/{id} — deletes a session.
     * HTTP 200 OK if deleted, 404 if not found.
     * @param id
     * @return 
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSession(@PathVariable Integer id) {
        return sessionRepository.findById(id)
                .map(s -> {
                    sessionRepository.delete(s);
                    return ResponseEntity.ok("Session deleted successfully"); // 200 OK
                })
                .orElse(ResponseEntity.notFound().build()); // 404 Not Found
    }

    /**
     * GET /API/sessions/{id}/attendance — returns all attendance
     * records for a specific session.
     * HTTP 200 OK on success.
     * @param id
     * @return 
     */
    @GetMapping("/{id}/attendance")
    public ResponseEntity<List<Attendance>> getAttendanceBySession(
            @PathVariable Integer id) {
        List<Attendance> records = attendanceRepository.findBySessionId(id);
        return ResponseEntity.ok(records); // 200 OK
    }

    /**
     * POST /API/sessions/{id}/attendance — logs a client's attendance
     * for a session.
     * HTTP 201 Created on success.
     * @param id
     * @param attendance
     * @return 
     */
    @PostMapping("/{id}/attendance")
    public ResponseEntity<Attendance> logAttendance(@PathVariable Integer id,
                                                     @RequestBody Attendance attendance) {
        attendance.setAttended(true);
        Attendance saved = attendanceRepository.save(attendance);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved); // 201 Created
    }
}