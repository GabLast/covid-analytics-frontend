package com.myorg.dto.request.security;

import com.myorg.dto.JsonRequest;
import com.myorg.dto.response.security.ProfileRow;
import lombok.Builder;

import java.util.List;

@Builder
public record UserRequest(
        Long id,
        String username,
        String password,
        String name,
        String email,
        boolean admin,
        List<ProfileRow> profiles,
        List<ProfileRow> profilesDelete
) implements JsonRequest {
}
