package com.myorg.dto.response.security;

import com.myorg.dto.JsonResponse;
import com.myorg.dto.ResponseInfo;
import lombok.Builder;

@Builder
public record UserResponse(UserResponseData data, ResponseInfo responseInfo) implements JsonResponse {
}
