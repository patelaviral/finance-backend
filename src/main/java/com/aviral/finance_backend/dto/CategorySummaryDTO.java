package com.aviral.finance_backend.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CategorySummaryDTO {
    private String category;
    private Double total;

}
