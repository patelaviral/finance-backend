package com.aviral.finance_backend.repository;

import com.aviral.finance_backend.model.FinancialRecord;
import com.aviral.finance_backend.model.enums.RecordType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Long> {

    Page<FinancialRecord> findByType(RecordType type, Pageable pageable);

    Page<FinancialRecord> findByCategory(String category, Pageable pageable);

    List<FinancialRecord> findByDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT r.category, SUM(r.amount) FROM FinancialRecord r GROUP BY r.category")
    List<Object[]> getCategoryWiseTotals();

    @Query("SELECT MONTH(r.date), r.type, SUM(r.amount) FROM FinancialRecord r GROUP BY MONTH(r.date), r.type")
    List<Object[]> getMonthlyTrends();

    @Query("SELECT SUM(r.amount) FROM FinancialRecord r WHERE r.type = :type AND r.date BETWEEN :start AND :end")
    Double getTotalByTypeAndDate(
            @Param("type") RecordType type,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    List<FinancialRecord> findTop5ByOrderByDateDesc();
}
