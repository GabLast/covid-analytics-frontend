package com.myorg.dto.response.configuration;

import com.myorg.dto.JsonResponse;
import lombok.Builder;

@Builder
public record CountryFindAllDataDetails(Long id, String name, String code) implements
        JsonResponse {
    @Override
    public String toString() {
        return name;
    }
}
