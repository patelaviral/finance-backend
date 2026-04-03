package com.aviral.finance_backend.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MonthlyTrendDTO {
    private int month;
    private String type;
    private Double total;
}
