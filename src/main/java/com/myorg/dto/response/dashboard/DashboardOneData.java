package com.myorg.dto.response.dashboard;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record DashboardOneData(
        BigDecimal infections,
        BigDecimal deaths,
        BigDecimal partialVaccinations,
        BigDecimal fullVaccinations,
        BigDecimal vaccinesDosesAdministrated
) {
}
