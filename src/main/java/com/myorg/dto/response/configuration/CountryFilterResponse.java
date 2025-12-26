package com.myorg.dto.response.configuration;

import com.myorg.dto.JsonResponse;
import com.myorg.dto.ResponseInfo;
import lombok.Builder;

@Builder
public record CountryFilterResponse(CountryFilterData data, ResponseInfo responseInfo) implements JsonResponse {
}
