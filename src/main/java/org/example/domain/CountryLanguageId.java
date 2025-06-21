package org.example.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class CountryLanguageId implements Serializable {
    private String countryCode;
    private String language;
}
