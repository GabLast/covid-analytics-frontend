package com.myorg.service;

import com.myorg.dto.ResponseResults;
import com.myorg.dto.response.security.LoginResponse;
import com.myorg.dto.security.LoginRequest;
import com.myorg.dto.security.PermitResponse;

public interface CovidAnalyticsService {

    //Auth
    LoginResponse authenticateUser(LoginRequest request);

    //Processes

    //Security
    PermitResponse getPermits();

    //Settings

}
