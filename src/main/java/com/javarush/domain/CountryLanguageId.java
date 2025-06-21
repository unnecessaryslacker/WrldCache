package com.javarush.domain;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@EqualsAndHashCode
public class CountryLanguageId implements Serializable {
    private String countryCode;
    private String language;
}
