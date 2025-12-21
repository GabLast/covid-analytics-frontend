package com.myorg.dto.response.dashboard;

import com.myorg.dto.ResponseInfo;
import lombok.Builder;

@Builder
public record DashboardOneResponse(
        DashboardOneData data,
        ResponseInfo responseInfo
) {
}
