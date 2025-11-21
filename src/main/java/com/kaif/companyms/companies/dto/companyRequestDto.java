package com.kaif.companyms.companies.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

// This DTO was added from the monolith project
@Getter
@Setter
public class companyRequestDto {

    @NotEmpty(message = "Company name cannot be empty")
    private String name;

    @NotEmpty(message = "Company description cannot be empty")
    private String description;

    @NotEmpty(message = "CEO name cannot be empty")
    private String ceo;

    private Integer foundedYear;
}