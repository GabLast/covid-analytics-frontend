package com.myorg.dto.response.configuration;

import com.myorg.dto.ResponseInfo;
import lombok.Builder;

@Builder
public record CountryFindAllResponse(CountryFindAllData data, ResponseInfo responseInfo) {
}
