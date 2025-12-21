package com.myorg.dto.response.security;

import lombok.Builder;

@Builder
public record UserFindAllResponse(UserFindAllData data) {
}
