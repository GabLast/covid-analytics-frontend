package com.myorg.dto.request;

import com.myorg.dto.JsonRequest;
import lombok.Data;

@Data
public abstract class RequestPagination implements JsonRequest {
    private String sortProperty = "id";
    private String sortOrder = "DESC";
    private Integer offset = 0;
    private Integer limit = 20;
}
