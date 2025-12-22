package com.myorg.dto.response.security;

import com.myorg.dto.JsonResponse;
import lombok.Builder;

@Builder
public record UserFilterDataDetails(
        Long id, String name, String email, boolean admin
) implements JsonResponse {
}
