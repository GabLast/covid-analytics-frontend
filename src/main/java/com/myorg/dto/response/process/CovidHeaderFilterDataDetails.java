package com.myorg.dto.response.process;

import com.myorg.dto.JsonResponse;
import lombok.Builder;

@Builder
public record CovidHeaderFilterDataDetails(Long id, String description, String loadDate, String userName, Long userId) implements
        JsonResponse {
}
