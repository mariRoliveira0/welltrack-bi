/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.welltrack.welltrack_bi.repository;

/**
 *
 * @author marianarodriguesoliveira
 */


import com.welltrack.welltrack_bi.model.FinancialTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface FinancialTransactionRepository extends JpaRepository<FinancialTransaction, Integer> {

    // Find all transactions for a client
    List<FinancialTransaction> findByClientId(Integer clientId);

    // Find transactions between two dates
    List<FinancialTransaction> findByTransactionDateBetween(LocalDate start, LocalDate end);

    // Total revenue for a client (CLV)
    @Query("SELECT SUM(t.amount) FROM FinancialTransaction t WHERE t.client.id = :clientId")
    BigDecimal getTotalRevenueByClient(Integer clientId);

    // Total revenue by service
    @Query("SELECT t.service.name, SUM(t.amount) FROM FinancialTransaction t GROUP BY t.service.name")
    List<Object[]> getRevenueByService();

    // Total revenue by month
    @Query("SELECT MONTH(t.transactionDate), YEAR(t.transactionDate), SUM(t.amount) FROM FinancialTransaction t GROUP BY YEAR(t.transactionDate), MONTH(t.transactionDate) ORDER BY YEAR(t.transactionDate), MONTH(t.transactionDate)")
    List<Object[]> getRevenueByMonth();

    // Total revenue by practitioner
    @Query("SELECT t.practitioner.fullName, SUM(t.amount) FROM FinancialTransaction t GROUP BY t.practitioner.fullName")
    List<Object[]> getRevenueByPractitioner();
}