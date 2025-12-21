package com.myorg.dto.response.process;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CovidDetailFilterDataDetails(
        Long id,

        String country,
        String countryCode,
        String date,

        BigDecimal new_tested
        ,BigDecimal new_confirmed
        ,BigDecimal new_persons_vaccinated
        ,BigDecimal new_deceased
        ,BigDecimal new_persons_fully_vaccinated
        ,BigDecimal new_vaccine_doses_administered
        ,BigDecimal population
) {
}
