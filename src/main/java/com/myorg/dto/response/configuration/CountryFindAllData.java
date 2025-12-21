package com.myorg.dto.response.configuration;

import com.myorg.dto.JsonResponse;
import lombok.Builder;

import java.util.List;

@Builder
public record CountryFindAllData(List<CountryFindAllDataDetails> dataList) implements
        JsonResponse {
}
