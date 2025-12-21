package com.myorg.dto.response;

import com.myorg.dto.JsonResponse;
import lombok.Builder;

@Builder
public record CountResponseData(Integer total) implements JsonResponse {
}
