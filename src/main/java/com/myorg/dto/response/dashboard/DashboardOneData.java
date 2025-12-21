package com.myorg.dto.response.dashboard;

import com.myorg.dto.JsonResponse;
import lombok.Builder;

import java.util.List;

@Builder
public record DashboardOneData(
        List<DashboardOneDataDetails> details
) implements JsonResponse {
}
