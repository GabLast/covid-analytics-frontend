package com.myorg.dto.response.security;

import com.myorg.dto.JsonResponse;
import lombok.Builder;

@Builder
public record PermitFetchDetail(Long id, String permit, String code) implements
        JsonResponse {
}
