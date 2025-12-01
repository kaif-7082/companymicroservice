package com.kaif.companyms.companies;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;

    private String ceo;
    private Integer foundedYear;
    private Double rating;
    @Lob // Tells JPA this can be a large object
    private byte[] logoData;
    private String logoType;
}
