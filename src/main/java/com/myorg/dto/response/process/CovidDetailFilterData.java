package com.myorg.dto.response.process;

import com.myorg.dto.JsonResponse;
import lombok.Builder;

import java.util.List;

@Builder
public record CovidDetailFilterData(List<CovidDetailFilterDataDetails> dataList) implements
        JsonResponse {
}
