package com.myorg.dto.response.configuration;

import lombok.Builder;

@Builder
public record CountryFindAllDataDetails(Long id, String name, String code) {
    @Override
    public String toString() {
        return name;
    }
}
