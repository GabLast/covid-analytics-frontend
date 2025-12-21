package com.myorg.dto.response.process;

import com.myorg.dto.JsonResponse;
import lombok.Builder;

import java.util.List;

@Builder
public record CovidHeaderFilterData(List<CovidHeaderFilterDataDetails> dataList) implements
        JsonResponse {
}
