package com.kaif.companyms.companies.dto;

import lombok.Getter;
import lombok.Setter;

// This DTO was added from the monolith project
@Getter
@Setter
public class companyResponseDto {
    private Long id;
    private String name;
    private String description;
    private String ceo;
    private Integer foundedYear;
}