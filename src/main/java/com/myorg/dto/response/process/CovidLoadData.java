package com.myorg.dto.response.process;

import com.myorg.dto.JsonResponse;
import lombok.Builder;

import java.util.List;

@Builder
public record CovidLoadData(
        Long headerId,
        String loadDate,
        String description,
        String jsonString,
        String jsonURL,
        List<CovidLoadDataDetail> details
) implements JsonResponse {
}
