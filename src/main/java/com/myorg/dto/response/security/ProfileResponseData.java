package com.myorg.dto.response.security;

import com.myorg.dto.JsonResponse;
import lombok.Builder;

import java.util.List;

@Builder
public record ProfileResponseData(Long id, String name, String description, List<PermitRow> permits) implements JsonResponse {
}
