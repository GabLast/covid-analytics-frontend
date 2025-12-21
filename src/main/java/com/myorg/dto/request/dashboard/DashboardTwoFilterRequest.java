package com.myorg.dto.request.dashboard;

import com.myorg.dto.JsonRequest;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record DashboardTwoFilterRequest(
        String country, LocalDate dateStart, LocalDate dateEnd
) implements JsonRequest {

}
