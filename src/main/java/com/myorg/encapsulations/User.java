package com.myorg.encapsulations;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Builder
public record User(String token, String name, Set<String> grantedAuthorities) {
}
