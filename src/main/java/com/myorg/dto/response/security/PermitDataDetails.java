package com.myorg.dto.response.security;

import com.myorg.dto.JsonResponse;

public record PermitDataDetails(String permit, String fatherCode) implements JsonResponse {
}
