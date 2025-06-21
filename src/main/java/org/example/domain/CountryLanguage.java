package org.example.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "countrylanguage")
@IdClass(CountryLanguageId.class)
public class CountryLanguage {

    @Id
    @Column(name = "countrycode")
    private String countryCode;

    @Id
    private String language;

    private boolean isOfficial;

    private double percentage;
}
