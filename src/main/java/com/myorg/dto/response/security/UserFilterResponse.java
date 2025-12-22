package com.myorg.dto.response.security;

import com.myorg.dto.JsonResponse;
import lombok.Builder;

@Builder
public record UserFilterResponse(UserFilterData data) implements JsonResponse {
}
