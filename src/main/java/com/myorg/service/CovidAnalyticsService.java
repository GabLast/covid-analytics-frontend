package com.myorg.service;

import com.myorg.dto.request.dashboard.DashboardTwoFilterRequest;
import com.myorg.dto.request.process.CovidDetailFilterRequest;
import com.myorg.dto.request.process.CovidHeaderFilterRequest;
import com.myorg.dto.request.process.CovidLoadRequest;
import com.myorg.dto.request.security.LoginRequest;
import com.myorg.dto.response.CountResponse;
import com.myorg.dto.response.configuration.CountryFindAllResponse;
import com.myorg.dto.response.dashboard.DashboardOneResponse;
import com.myorg.dto.response.dashboard.DashboardTwoResponse;
import com.myorg.dto.response.process.CovidDetailFilterResponse;
import com.myorg.dto.response.process.CovidHeaderFilterResponse;
import com.myorg.dto.response.process.CovidLoadResponse;
import com.myorg.dto.response.security.LoginResponse;
import com.myorg.dto.response.security.PermitResponse;
import com.myorg.dto.response.security.UserFindAllResponse;


public interface CovidAnalyticsService {

    //Auth
    LoginResponse authenticateUser(LoginRequest request);

    //Processes
    CovidHeaderFilterResponse findAllHeaderFilter(CovidHeaderFilterRequest request);

    CountResponse countAllHeaderFilter(CovidHeaderFilterRequest request);

    CovidLoadResponse getHeaderData(Long id);

    CovidLoadResponse deleteHeader(Long id);

    CovidLoadResponse loadData(CovidLoadRequest request, byte[] file);

    CovidDetailFilterResponse findAllDetailFilter(CovidDetailFilterRequest request);

    CountResponse countAllDetailFilter(CovidDetailFilterRequest request);

    //Settings

    //Dashboard
    DashboardOneResponse getBoardOneData();
    DashboardTwoResponse getBoardTwoData(DashboardTwoFilterRequest request);

    //Data Fetch
    PermitResponse getPermits();
    UserFindAllResponse getUsers();
    CountryFindAllResponse getCountries();

}
