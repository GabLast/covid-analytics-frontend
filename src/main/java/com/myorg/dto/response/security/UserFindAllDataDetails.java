package com.myorg.dto.response.security;

import com.myorg.dto.JsonResponse;
import lombok.Builder;

@Builder
public record UserFindAllDataDetails(Long id, String name) implements JsonResponse {
    @Override
    public String toString() {
        return name;
    }
}
