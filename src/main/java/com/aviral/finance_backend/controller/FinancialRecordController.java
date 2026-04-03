package com.aviral.finance_backend.controller;

import com.aviral.finance_backend.dto.CategorySummaryDTO;
import com.aviral.finance_backend.dto.DashboardDTO;
import com.aviral.finance_backend.dto.MonthlyTrendDTO;
import com.aviral.finance_backend.model.FinancialRecord;
import com.aviral.finance_backend.model.User;
import com.aviral.finance_backend.model.enums.RecordType;
import com.aviral.finance_backend.service.FinancialRecordService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/records")
public class FinancialRecordController {

    @Autowired
    private FinancialRecordService recordService;

    // ✅ Helper method (cleaner)
    private User getUser(HttpServletRequest request) {
        return (User) request.getAttribute("user");
    }

    @GetMapping
    public List<FinancialRecord> getAllRecords(HttpServletRequest request) {
        User user = getUser(request);
        return recordService.getAllRecords(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRecord(
            @PathVariable Long id,
            HttpServletRequest request) {

        User user = getUser(request);
        recordService.deleteRecord(id, user);
        return ResponseEntity.ok("Record deleted successfully");
    }

    @GetMapping("/type/{type}")
    public Page<FinancialRecord> getByType(
            @PathVariable RecordType type,
            HttpServletRequest request,
            Pageable pageable) {

        User user = getUser(request);
        return recordService.getByType(type, user, pageable);
    }

    @GetMapping("/category/{category}")
    public Page<FinancialRecord> getByCategory(
            @PathVariable String category,
            HttpServletRequest request,
            Pageable pageable) {

        User user = getUser(request);
        return recordService.getByCategory(category, user, pageable);
    }

    @GetMapping("/date")
    public List<FinancialRecord> getByDateRange(
            @RequestParam LocalDate start,
            @RequestParam LocalDate end,
            HttpServletRequest request) {

        User user = getUser(request);
        return recordService.getByDateRange(start, end, user);
    }

    // ✅ Added user validation context (via request)
    @GetMapping("/summary/income")
    public Double getTotalIncome(
            @RequestParam LocalDate start,
            @RequestParam LocalDate end,
            HttpServletRequest request) {

        User user = getUser(request);
        return recordService.getTotalIncome(start, end);
    }

    @GetMapping("/summary/expense")
    public Double getTotalExpense(HttpServletRequest request) {
        User user = getUser(request);
        return recordService.getTotalExpense();
    }

    @GetMapping("/summary/net")
    public Double getNetBalance(HttpServletRequest request) {
        User user = getUser(request);
        return recordService.getNetBalance();
    }

    @GetMapping("/summary/category")
    public List<CategorySummaryDTO> getCategorySummary(HttpServletRequest request) {
        User user = getUser(request);
        return recordService.getCategorySummary();
    }

    @PostMapping
    public FinancialRecord createRecord(
            @Valid @RequestBody FinancialRecord record,
            HttpServletRequest request) {

        User user = getUser(request);
        return recordService.createRecord(record, user);
    }

    @PutMapping("/{id}")
    public FinancialRecord updateRecord(
            @PathVariable Long id,
            @Valid @RequestBody FinancialRecord record,
            HttpServletRequest request) {

        User user = getUser(request);
        return recordService.updateRecord(id, record, user);
    }

    @GetMapping("/summary/recent")
    public List<FinancialRecord> getRecentActivity(HttpServletRequest request) {
        User user = getUser(request);
        return recordService.getRecentActivity(user);
    }

    @GetMapping("/summary/monthly")
    public List<MonthlyTrendDTO> getMonthlyTrends(HttpServletRequest request) {
        User user = getUser(request);
        return recordService.getMonthlyTrends(user);
    }

    @GetMapping("/summary/dashboard")
    public DashboardDTO getDashboard(HttpServletRequest request) {
        User user = getUser(request);
        return recordService.getDashboard(user);
    }
}
