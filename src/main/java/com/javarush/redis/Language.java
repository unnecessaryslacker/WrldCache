package com.javarush.redis;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class Language {
    private String language;
    private Boolean official;
    private BigDecimal percentage;
}
