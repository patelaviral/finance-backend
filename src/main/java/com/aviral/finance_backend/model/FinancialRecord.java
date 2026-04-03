package com.aviral.finance_backend.model;
import jakarta.validation.constraints.*;


import com.aviral.finance_backend.model.enums.RecordType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class FinancialRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Positive
    private Double amount;

    @NotBlank
    private String category;

    @NotNull
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private RecordType type;

    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
