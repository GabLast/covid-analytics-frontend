package com.myorg.dto.response.process;

import lombok.Builder;

import java.util.List;

@Builder
public record CovidHeaderFilterData(List<CovidHeaderFilterDataDetails> dataList) {
}
