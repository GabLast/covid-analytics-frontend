package com.myorg.dto.response.security;

import com.myorg.dto.JsonResponse;

import java.util.List;

public record PermitData(List<PermitRow> permits) implements JsonResponse {
}
