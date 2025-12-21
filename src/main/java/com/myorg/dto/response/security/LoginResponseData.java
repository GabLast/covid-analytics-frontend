package com.myorg.dto.response.security;

import com.myorg.dto.JsonResponse;
import lombok.Builder;

import java.util.Set;

@Builder
public record LoginResponseData(String token, String name, Set<String> grantedAuthorities)
        implements JsonResponse {
}
