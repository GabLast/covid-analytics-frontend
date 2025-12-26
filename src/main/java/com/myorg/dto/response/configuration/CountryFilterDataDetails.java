package com.myorg.dto.response.configuration;

import com.myorg.dto.JsonResponse;
import lombok.Builder;

@Builder
public record CountryFilterDataDetails(
        Long id,
        String name,
        String code,
        String placeId,
        String wikiDataId,
        String dataCommonsId,
        String iso_3166_1_alpha_2,
        String iso_3166_1_alpha_3
) implements JsonResponse {
}
