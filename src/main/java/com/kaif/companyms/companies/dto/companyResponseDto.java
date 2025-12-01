package com.kaif.companyms.companies.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class companyResponseDto {
    private Long id;
    private String name;
    private String description;
    private String ceo;
    private Integer foundedYear;
}