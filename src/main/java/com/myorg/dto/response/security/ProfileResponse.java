package com.myorg.dto.response.security;

import com.myorg.dto.JsonResponse;
import com.myorg.dto.ResponseInfo;
import lombok.Builder;

@Builder
public record ProfileResponse(ProfileResponseData data, ResponseInfo responseInfo) implements JsonResponse {
}
