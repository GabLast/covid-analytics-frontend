package com.myorg.dto.response.security;

import com.myorg.dto.JsonResponse;
import com.myorg.dto.ResponseInfo;

public record PermitResponse(PermitData data, ResponseInfo responseInfo) implements
        JsonResponse {
}
