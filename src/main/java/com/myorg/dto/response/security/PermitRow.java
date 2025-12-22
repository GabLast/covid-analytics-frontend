package com.myorg.dto.response.security;

import com.myorg.dto.JsonResponse;
import lombok.Builder;

@Builder
public record PermitRow(Long profilePermitId, Long id, String permit, String code) implements JsonResponse {
    @Override
    public String toString() {
        return permit;
    }
}
