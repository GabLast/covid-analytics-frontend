package com.myorg.dto.response.security;

import com.myorg.dto.JsonResponse;
import lombok.Builder;

@Builder
public record ProfileFetchDataDetails(Long id, String name) implements JsonResponse {
}
