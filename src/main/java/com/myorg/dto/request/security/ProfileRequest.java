package com.myorg.dto.request.security;

import com.myorg.dto.JsonRequest;
import com.myorg.dto.response.security.PermitRow;
import lombok.Builder;

import java.util.List;

@Builder
public record ProfileRequest(
        Long id,
        String name,
        String description,
        List<PermitRow> permits,
        List<PermitRow> permitsDelete
) implements JsonRequest {
}
