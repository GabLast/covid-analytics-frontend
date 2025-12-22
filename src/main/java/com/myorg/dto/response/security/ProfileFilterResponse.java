package com.myorg.dto.response.security;

import com.myorg.dto.JsonResponse;
import lombok.Builder;

@Builder
public record ProfileFilterResponse(ProfileFilterData data) implements JsonResponse {
}
