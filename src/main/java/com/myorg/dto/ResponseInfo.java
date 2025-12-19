package com.myorg.dto;

import com.vaadin.copilot.shaded.checkerframework.checker.units.qual.N;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Builder
public record ResponseInfo(String message, String path, int status)
        implements JsonResponse {

}
