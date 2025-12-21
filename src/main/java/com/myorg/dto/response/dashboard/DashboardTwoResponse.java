package com.myorg.dto.response.dashboard;

import com.myorg.dto.ResponseInfo;
import lombok.Builder;

@Builder
public record DashboardTwoResponse(
        DashboardOneData data,
        ResponseInfo responseInfo
) {
}
