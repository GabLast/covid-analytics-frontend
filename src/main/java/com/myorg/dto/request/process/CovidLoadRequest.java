package com.myorg.dto.request.process;

import com.myorg.dto.JsonRequest;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record CovidLoadRequest(Long id, LocalDate date, String description, String jsonURL, String jsonString)
        implements JsonRequest {
}
