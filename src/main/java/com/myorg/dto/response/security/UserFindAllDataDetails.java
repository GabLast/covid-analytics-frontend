package com.myorg.dto.response.security;

import lombok.Builder;

@Builder
public record UserFindAllDataDetails(Long id, String name) {
    @Override
    public String toString() {
        return name;
    }
}
