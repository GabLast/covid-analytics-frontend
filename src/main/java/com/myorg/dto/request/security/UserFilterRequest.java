package com.myorg.dto.request.security;

import com.myorg.dto.JsonRequest;
import com.myorg.dto.request.RequestPagination;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserFilterRequest extends RequestPagination implements JsonRequest {
    private boolean   enabled   = true;
    private String    name   = null;
    private String    mail   = null;
}
