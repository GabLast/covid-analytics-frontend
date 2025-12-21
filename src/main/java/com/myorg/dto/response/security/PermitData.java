package com.myorg.dto.response.security;

import com.myorg.dto.JsonResponse;

import java.util.Set;

public record PermitData(Set<String> permits) implements JsonResponse {
}
