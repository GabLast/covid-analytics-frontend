package com.myorg.dto.response.security;

import com.myorg.dto.JsonResponse;
import lombok.Builder;

import java.util.List;

@Builder
public record UserResponseData(
        Long id,
        String username,
        String name,
        String mail,
        String password,
        boolean admin,
        List<ProfileRow> profiles
) implements JsonResponse {
}
