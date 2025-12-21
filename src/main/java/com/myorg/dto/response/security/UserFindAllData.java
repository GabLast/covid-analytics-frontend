package com.myorg.dto.response.security;

import lombok.Builder;

import java.util.List;

@Builder
public record UserFindAllData(List<UserFindAllDataDetails> dataList) {
}
