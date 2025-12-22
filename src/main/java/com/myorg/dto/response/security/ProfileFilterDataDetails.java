package com.myorg.dto.response.security;

import com.myorg.dto.JsonResponse;
import lombok.Builder;

@Builder
public record ProfileFilterDataDetails(
        Long id, String name, String description
) implements JsonResponse {
}
