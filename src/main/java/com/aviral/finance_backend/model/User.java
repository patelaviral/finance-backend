package com.aviral.finance_backend.model;
import jakarta.validation.constraints.*;

import com.aviral.finance_backend.model.enums.Role;
import com.aviral.finance_backend.model.enums.Status;
import jakarta.persistence.*;
import lombok.Data;


@Entity
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private Status status;
}
