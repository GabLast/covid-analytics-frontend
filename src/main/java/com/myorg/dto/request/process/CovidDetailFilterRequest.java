package com.myorg.dto.request.process;

import com.myorg.dto.JsonRequest;
import com.myorg.dto.request.RequestPagination;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
public class CovidDetailFilterRequest extends RequestPagination implements JsonRequest {
    private boolean   enabled   = true;
    private Long      headerId  = null;
    private String    country   = null;
    private LocalDate dateStart = null;
    private LocalDate dateEnd   = null;
}
