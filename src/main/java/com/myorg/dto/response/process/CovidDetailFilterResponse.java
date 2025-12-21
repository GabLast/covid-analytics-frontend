package com.myorg.dto.response.process;

import com.myorg.dto.JsonResponse;
import lombok.Builder;

@Builder
public record CovidDetailFilterResponse(CovidDetailFilterData data) implements
        JsonResponse {
}
