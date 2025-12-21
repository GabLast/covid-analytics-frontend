package com.myorg.dto.response.process;

import lombok.Builder;

@Builder
public record CovidHeaderFilterDataDetails(Long id, String description, String loadDate, String userName, Long userId) {
}
