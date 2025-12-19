package com.myorg.dto.security;

import java.util.Set;

public record PermitResponse(Set<String> permits) {
}
