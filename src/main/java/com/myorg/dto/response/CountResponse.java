package com.myorg.dto.response;

import com.myorg.dto.JsonResponse;
import com.myorg.dto.ResponseInfo;
import lombok.Builder;

@Builder
public record CountResponse(CountResponseData data, ResponseInfo responseInfo) implements
        JsonResponse {
}
