package com.myorg.dto;

import lombok.Builder;

@Builder
public record ResponseResults(Object response, ResponseInfo responseInfo) {
}
