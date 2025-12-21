package com.myorg.dto.response.dashboard;

import com.myorg.dto.JsonResponse;
import com.myorg.dto.ResponseInfo;
import lombok.Builder;

@Builder
public record DashboardTwoResponse(
        DashboardTwoData data,
        ResponseInfo responseInfo
) implements JsonResponse {
}
