package com.myorg.dto.security;

import com.myorg.dto.JsonRequest;
import lombok.Builder;

@Builder
public record LoginRequest(Long id, String usernameMail, String password)
        implements JsonRequest {
}
