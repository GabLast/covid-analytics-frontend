package com.myorg.dto.response.security;

import com.myorg.dto.JsonResponse;
import lombok.Builder;

import java.util.List;

@Builder
public record ProfileFilterData(List<ProfileFilterDataDetails> dataList) implements
        JsonResponse {
}
