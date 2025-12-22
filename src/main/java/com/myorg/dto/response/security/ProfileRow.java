package com.myorg.dto.response.security;

import com.myorg.dto.JsonResponse;
import lombok.Builder;

@Builder
public record ProfileRow(Long profileUserId, Long id, String profile) implements JsonResponse {
}
