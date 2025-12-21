package com.myorg.dto.request.dashboard;

import com.myorg.dto.JsonRequest;
import com.myorg.dto.request.RequestPagination;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
public class DashboardTwoFilterRequest implements JsonRequest {
    private String    country   = null;
    private LocalDate dateStart = null;
    private LocalDate dateEnd   = null;
}
