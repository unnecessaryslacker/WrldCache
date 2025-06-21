package org.example.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "country")
public class Country {

    @Id
    @Column(length = 3)
    private String code;

    private String name;

    private String continent;

    private String region;

    @Column(name = "surfacearea")
    private double surfaceArea;

    @Column(name = "indepyear")
    private Integer indepYear;

    private int population;

    @Column(name = "lifeexpectancy")
    private Double lifeExpectancy;

    @Column(name = "gnp")
    private Double gnp;

    @Column(name = "gnpold")
    private Double gnpOld;

    @Column(name = "localname")
    private String localName;

    private String governmentForm;

    @Column(name = "headofstate")
    private String headOfState;

    private Integer capital;

    @Column(name = "code2")
    private String code2;
}
