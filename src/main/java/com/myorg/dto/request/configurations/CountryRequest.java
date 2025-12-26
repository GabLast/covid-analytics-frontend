package com.myorg.dto.request.configurations;

import com.myorg.dto.JsonRequest;
import lombok.Builder;

@Builder
public record CountryRequest(
        Long id,
        String name,
        String code,
        String placeId,
        String wikiDataId,
        String dataCommonsId,
        String iso_3166_1_alpha_2,
        String iso_3166_1_alpha_3
) implements JsonRequest {
}
