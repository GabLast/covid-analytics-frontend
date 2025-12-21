package com.myorg.dto.response.process;

import com.myorg.dto.JsonResponse;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CovidLoadDataDetail(
        Long headerId,
        String country,
        String country_code,

        String date,
        BigDecimal new_tested,
        BigDecimal new_confirmed,
        BigDecimal new_deceased,
        BigDecimal new_persons_vaccinated,
        BigDecimal new_persons_fully_vaccinated,
        BigDecimal new_vaccine_doses_administered,

        BigDecimal population,
        BigDecimal population_male,
        BigDecimal population_female,
        BigDecimal population_rural,
        BigDecimal population_urban,
        BigDecimal population_largest_city,

        BigDecimal cancel_public_events,
        BigDecimal public_transport_closing,
        BigDecimal international_support,
        BigDecimal debt_relief,
        BigDecimal investment_in_vaccines
) implements JsonResponse {
}
