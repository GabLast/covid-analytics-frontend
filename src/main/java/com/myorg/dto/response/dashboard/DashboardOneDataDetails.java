package com.myorg.dto.response.dashboard;

import com.myorg.dto.JsonResponse;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record DashboardOneDataDetails(
        Long countryId,
        String country,
        BigDecimal population,
        BigDecimal populationMale,
        BigDecimal populationFemale
) implements JsonResponse {
}
