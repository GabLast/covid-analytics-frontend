package com.myorg.dto.response.process;

import com.myorg.dto.ResponseInfo;
import lombok.Builder;

@Builder
public record CovidHeaderFilterResponse(CovidHeaderFilterData data, ResponseInfo responseInfo) {
}
