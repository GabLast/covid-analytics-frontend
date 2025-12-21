package com.myorg.dto;

import lombok.Builder;

@Builder
public record ResponseInfo(String message, String path, int status)
        implements JsonResponse {

}
