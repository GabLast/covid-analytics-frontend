package com.myorg.dto.response;

import lombok.Builder;

@Builder
public record GenericResponse(
        String message, String path, int status
) {
}
