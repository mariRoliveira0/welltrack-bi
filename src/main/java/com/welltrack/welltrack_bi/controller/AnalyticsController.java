/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.welltrack.welltrack_bi.controller;

/**
 *
 * @author marianarodriguesoliveira
 */


import com.welltrack.welltrack_bi.repository.FinancialTransactionRepository;
import com.welltrack.welltrack_bi.repository.ClientRepository;
import com.welltrack.welltrack_bi.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

/**
 * AnalyticsController — REST API endpoints for the Financial
 * Analytics Dashboard and KPI Dashboard.
 * This is the core business intelligence layer of WellTrack BI.
 * All KPI formulas are implemented here as per the SRS requirements.
 */
@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private FinancialTransactionRepository transactionRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private SessionRepository sessionRepository;

    /**
     * GET /api/analytics/revenue/monthly — returns total revenue
     * grouped by month and year.
     * Used by the Financial Analytics Dashboard line chart.
     * HTTP 200 OK on success.
     * @return 
     */
    @GetMapping("/revenue/monthly")
    public ResponseEntity<List<Map<String, Object>>> getMonthlyRevenue() {
        List<Object[]> results = transactionRepository.getRevenueByMonth();
        List<Map<String, Object>> data = new ArrayList<>();

        for (Object[] row : results) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("month", row[0]);
            entry.put("year",  row[1]);
            entry.put("revenue", row[2]);
            data.add(entry);
        }
        return ResponseEntity.ok(data); // 200 OK
    }

    /**
     * GET /api/analytics/revenue/by-service — returns total revenue
     * grouped by service type.
     * Used by the Financial Analytics Dashboard bar chart.
     * HTTP 200 OK on success.
     * @return 
     */
    @GetMapping("/revenue/by-service")
    public ResponseEntity<List<Map<String, Object>>> getRevenueByService() {
        List<Object[]> results = transactionRepository.getRevenueByService();
        List<Map<String, Object>> data = new ArrayList<>();

        for (Object[] row : results) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("service", row[0]);
            entry.put("revenue", row[1]);
            data.add(entry);
        }
        return ResponseEntity.ok(data); // 200 OK
    }

    /**
     * GET /api/analytics/revenue/by-practitioner — returns total revenue
     * grouped by practitioner.
     * Used by the Financial Analytics Dashboard bar chart.
     * HTTP 200 OK on success.
     */
    @GetMapping("/revenue/by-practitioner")
    public ResponseEntity<List<Map<String, Object>>> getRevenueByPractitioner() {
        List<Object[]> results = transactionRepository.getRevenueByPractitioner();
        List<Map<String, Object>> data = new ArrayList<>();

        for (Object[] row : results) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("practitioner", row[0]);
            entry.put("revenue",      row[1]);
            data.add(entry);
        }
        return ResponseEntity.ok(data); // 200 OK
    }

    /**
     * GET /API/analytics/clv/{clientId} — calculates Client Lifetime
     * Value (CLV) for a specific client.
     * CLV = total amount spent by the client across all transactions.
     * HTTP 200 OK if found, 404 if client has no transactions.
     */
    @GetMapping("/clv/{clientId}")
    public ResponseEntity<Map<String, Object>> getClientLifetimeValue(
            @PathVariable Integer clientId) {

        BigDecimal clv = transactionRepository.getTotalRevenueByClient(clientId);
        if (clv == null) clv = BigDecimal.ZERO;

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("clientId", clientId);
        result.put("clv", clv);
        return ResponseEntity.ok(result); // 200 OK
    }

    /**
     * GET /api/analytics/kpi — calculates all KPI metrics for the
     * KPI Dashboard.
     *
     * KPIs returned:
     * - MRR: total revenue in the current month
     * - Retention Rate: returning clients / total clients * 100
     * - Churn Rate: 100 - retention rate
     * - Utilisation Rate: sessions with attendance / total sessions * 100
     * - Revenue Growth Rate: (this month - last month) / last month * 100
     *
     * HTTP 200 OK on success.
     */
    @GetMapping("/kpi")
    public ResponseEntity<Map<String, Object>> getKpiDashboard() {
        Map<String, Object> kpi = new LinkedHashMap<>();

        LocalDate now       = LocalDate.now();
        LocalDate startOfMonth     = now.withDayOfMonth(1);
        LocalDate startOfLastMonth = now.minusMonths(1).withDayOfMonth(1);
        LocalDate endOfLastMonth   = now.withDayOfMonth(1).minusDays(1);

        // ── MRR: total revenue this month ────────────────────────────
        List<Object[]> monthly = transactionRepository.getRevenueByMonth();
        BigDecimal mrr = BigDecimal.ZERO;
        BigDecimal lastMonthRevenue = BigDecimal.ZERO;

        for (Object[] row : monthly) {
            int month = ((Number) row[0]).intValue();
            int year  = ((Number) row[1]).intValue();
            BigDecimal amount = (BigDecimal) row[2];

            if (month == now.getMonthValue() && year == now.getYear()) {
                mrr = amount;
            }
            if (month == now.minusMonths(1).getMonthValue()
                    && year == now.minusMonths(1).getYear()) {
                lastMonthRevenue = amount;
            }
        }
        kpi.put("mrr", mrr);

        // ── Revenue Growth Rate ───────────────────────────────────────
        // Formula: (this month - last month) / last month * 100
        BigDecimal growthRate = BigDecimal.ZERO;
        if (lastMonthRevenue.compareTo(BigDecimal.ZERO) > 0) {
            growthRate = mrr.subtract(lastMonthRevenue)
                    .divide(lastMonthRevenue, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
        }
        kpi.put("revenueGrowthRate", growthRate);

        // ── Retention Rate ────────────────────────────────────────────
        // Formula: clients who returned this month / total active clients * 100
        long totalClients = clientRepository.countByActiveTrue();
        long returningClients = transactionRepository
                .findByTransactionDateBetween(startOfMonth, now)
                .stream()
                .map(t -> t.getClient().getId())
                .distinct()
                .count();

        BigDecimal retentionRate = BigDecimal.ZERO;
        if (totalClients > 0) {
            retentionRate = BigDecimal.valueOf(returningClients)
                    .divide(BigDecimal.valueOf(totalClients), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
        }
        kpi.put("retentionRate", retentionRate);

        // ── Churn Rate ────────────────────────────────────────────────
        // Formula: 100 - retention rate
        BigDecimal churnRate = BigDecimal.valueOf(100)
                .subtract(retentionRate)
                .setScale(2, RoundingMode.HALF_UP);
        kpi.put("churnRate", churnRate);

        // ── Utilisation Rate ──────────────────────────────────────────
        // Formula: sessions that had at least 1 attendee / total sessions * 100
        long totalSessions = sessionRepository.count();
        long sessionsWithAttendance = sessionRepository
                .findBySessionDateBetween(
                        now.minusMonths(3), now)
                .stream()
                .filter(s -> !s.getService().getName().isEmpty())
                .count();

        BigDecimal utilisationRate = BigDecimal.ZERO;
        if (totalSessions > 0) {
            utilisationRate = BigDecimal.valueOf(sessionsWithAttendance)
                    .divide(BigDecimal.valueOf(totalSessions), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
        }
        kpi.put("utilisationRate", utilisationRate);

        return ResponseEntity.ok(kpi); // 200 OK
    }
}
