package com.aviral.finance_backend.dto;

import com.aviral.finance_backend.model.FinancialRecord;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DashboardDTO {
    private Double totalIncome;
    private Double totalExpense;
    private Double netBalance;
    private List<CategorySummaryDTO> categorySummary;
    private List<FinancialRecord> recentActivity;
    private List<MonthlyTrendDTO> monthlyTrends;
}
