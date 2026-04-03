package com.aviral.finance_backend.service;

import com.aviral.finance_backend.dto.CategorySummaryDTO;
import com.aviral.finance_backend.dto.DashboardDTO;
import com.aviral.finance_backend.dto.MonthlyTrendDTO;
import com.aviral.finance_backend.exception.AccessDeniedException;
import com.aviral.finance_backend.exception.ResourceNotFoundException;
import com.aviral.finance_backend.model.FinancialRecord;
import com.aviral.finance_backend.model.User;
import com.aviral.finance_backend.model.enums.RecordType;
import com.aviral.finance_backend.model.enums.Role;
import com.aviral.finance_backend.model.enums.Status;
import com.aviral.finance_backend.repository.FinancialRecordRepository;
import com.aviral.finance_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class FinancialRecordService {
    @Autowired
    private FinancialRecordRepository recordRepository;

    @Autowired
    private UserRepository userRepository;

    public List<FinancialRecord> getAllRecords(User usr) {
        User user = validateUser(usr);
        return recordRepository.findAll();
    }

    public void deleteRecord(Long id, User usr) {

        User user = validateUser(usr);
        checkAdmin(user);

        if(!recordRepository.existsById(id)) {
            throw new ResourceNotFoundException("Record with id " + id + " not found");
        }

        recordRepository.deleteById(id);
    }

    // these are methods which is going to use for query in repository methods
    public Page<FinancialRecord> getByType(RecordType type, User usr, Pageable pageable) {
        User user = validateUser(usr);
        return recordRepository.findByType(type, pageable);
    }

    public Page<FinancialRecord> getByCategory(String category, User usr, Pageable pageable) {
        User user = validateUser(usr);
        return recordRepository.findByCategory(category, pageable);
    }

    public List<FinancialRecord> getByDateRange(LocalDate start, LocalDate end, User usr) {
        User user = validateUser(usr);
        return recordRepository.findByDateBetween(start, end);
    }

    // these are filter and specific query methods
    public Double getTotalIncome(LocalDate start, LocalDate end) {
        return recordRepository.getTotalByTypeAndDate(RecordType.INCOME, start, end);
    }
    public Double getTotalIncome() {
        LocalDate[] date = getDefaultRange();
        LocalDate start = date[0];  // default last 30 days
        LocalDate end = date[1];
        return getTotalIncome(start, end);
    }

    public Double getTotalExpense(LocalDate start, LocalDate end) {
        return recordRepository.getTotalByTypeAndDate(RecordType.EXPENSE, start, end);
    }
    public Double getTotalExpense() {
        LocalDate[] date = getDefaultRange();
        LocalDate start = date[0];  // default last 30 days
        LocalDate end = date[1];
        return getTotalExpense(start, end);
    }


    public Double getNetBalance() {
        Double income = getTotalIncome();
        Double expense = getTotalExpense();
        return (income != null ? income : 0) - (expense != null ? expense : 0);
    }

    public List<CategorySummaryDTO> getCategorySummary() {
        List<Object[]> data = recordRepository.getCategoryWiseTotals();

        return data.stream().map(obj -> {
            CategorySummaryDTO dto = new CategorySummaryDTO();
            dto.setCategory((String) obj[0]);
            dto.setTotal((Double) obj[1]);
            return dto;
        }).toList();
    }

    public FinancialRecord createRecord(FinancialRecord record, User usr) {

        User user = validateUser(usr);

        if(user.getRole() == Role.VIEWER) {
            throw new AccessDeniedException("Viewer cannot create records");
        }

        record.setUser(user);
        return recordRepository.save(record);
    }

    public FinancialRecord updateRecord(Long id, FinancialRecord updatedRecord, User usr) {

        User user = validateUser(usr);

        if(user.getRole() == Role.VIEWER) {
            throw new AccessDeniedException("Viewer cannot update records");
        }

        FinancialRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Record with id " + id + " not found"));

        if(!record.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You can only update your own records");
        }

        record.setAmount(updatedRecord.getAmount());
        record.setType(updatedRecord.getType());
        record.setCategory(updatedRecord.getCategory());
        record.setDescription(updatedRecord.getDescription());
        record.setDate(updatedRecord.getDate());

        return recordRepository.save(record);
    }

    public List<FinancialRecord> getRecentActivity(User usr) {
        User user = validateUser(usr);
        return recordRepository.findTop5ByOrderByDateDesc();
    }

    public List<MonthlyTrendDTO> getMonthlyTrends(User usr) {
        User user = validateUser(usr);

        return recordRepository.getMonthlyTrends()
                .stream()
                .map(obj -> {
                    MonthlyTrendDTO dto = new MonthlyTrendDTO();
                    dto.setMonth((Integer) obj[0]);
                    dto.setType(obj[1].toString());
                    dto.setTotal((Double) obj[2]);
                    return dto;
                }).toList();
    }

    public DashboardDTO getDashboard(User usr) {
        User user = validateUser(usr);

        DashboardDTO dto = new DashboardDTO();

        dto.setTotalIncome(getTotalIncome());
        dto.setTotalExpense(getTotalExpense());
        dto.setNetBalance(getNetBalance());
        dto.setCategorySummary(getCategorySummary());
        dto.setRecentActivity(getRecentActivity(user));
        dto.setMonthlyTrends(getMonthlyTrends(user));

        return dto;
    }

    private LocalDate[] getDefaultRange() {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(30);
        return new LocalDate[]{start, end};
    }

    // this below method will validate -> is user active/unactive and role?
    private User validateUser(User user) {
        if(user == null) throw new AccessDeniedException("Unauthorized");

        if(user.getStatus() == Status.INACTIVE) {
            throw new AccessDeniedException("User is inactive");
        }
        return user;
    }

    private void checkAdmin(User user) {
        if(user.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Admin access required");
        }
    }
}
