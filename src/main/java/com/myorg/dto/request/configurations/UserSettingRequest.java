package com.myorg.dto.request.configurations;

import com.myorg.dto.JsonRequest;
import lombok.Builder;

@Builder
public record UserSettingRequest(
        Long id,
        String timeZoneString,
        String dateFormat,
        String dateTimeFormat,
        boolean darkMode,
        String language
) implements JsonRequest {
}
