package com.myorg.dto.request.configurations;

import com.myorg.dto.JsonRequest;
import com.myorg.dto.request.RequestPagination;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CountryFilterRequest extends RequestPagination implements JsonRequest {
    private boolean enabled     = true;
    private String  name        = null;
    private String  countryCode = null;
}
