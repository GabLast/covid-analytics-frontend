package com.myorg.dto.response.security;

import com.myorg.dto.JsonResponse;
import com.myorg.dto.ResponseInfo;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Builder
public record LoginResponse(LoginResponseData data) implements JsonResponse {
}
