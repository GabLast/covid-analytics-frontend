package com.myorg.dto.response.security;

import com.myorg.dto.JsonResponse;
import lombok.Builder;

import java.util.List;

@Builder
public record UserFindAllData(List<UserFindAllDataDetails> dataList) implements
        JsonResponse {
}
