package com.myorg.dto.request.security;

import com.myorg.dto.JsonRequest;
import lombok.Builder;

@Builder
public record LoginRequest(Long id, String usernameMail, String password)
        implements JsonRequest {
}
