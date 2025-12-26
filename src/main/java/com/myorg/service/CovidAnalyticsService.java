package com.myorg.service;

import com.myorg.dto.request.configurations.CountryFilterRequest;
import com.myorg.dto.request.configurations.CountryRequest;
import com.myorg.dto.request.configurations.UserSettingRequest;
import com.myorg.dto.request.dashboard.DashboardTwoFilterRequest;
import com.myorg.dto.request.process.CovidDetailFilterRequest;
import com.myorg.dto.request.process.CovidHeaderFilterRequest;
import com.myorg.dto.request.process.CovidLoadRequest;
import com.myorg.dto.request.security.LoginRequest;
import com.myorg.dto.request.security.ProfileFilterRequest;
import com.myorg.dto.request.security.ProfileRequest;
import com.myorg.dto.request.security.UserFilterRequest;
import com.myorg.dto.request.security.UserRequest;
import com.myorg.dto.response.CountResponse;
import com.myorg.dto.response.configuration.CountryFilterResponse;
import com.myorg.dto.response.configuration.CountryFindAllResponse;
import com.myorg.dto.response.configuration.CountryResponse;
import com.myorg.dto.response.configuration.UserSettingResponse;
import com.myorg.dto.response.dashboard.DashboardOneResponse;
import com.myorg.dto.response.dashboard.DashboardTwoResponse;
import com.myorg.dto.response.process.CovidDetailFilterResponse;
import com.myorg.dto.response.process.CovidHeaderFilterResponse;
import com.myorg.dto.response.process.CovidLoadResponse;
import com.myorg.dto.response.security.LoginResponse;
import com.myorg.dto.response.security.PermitFetchResponse;
import com.myorg.dto.response.security.ProfileFetchResponse;
import com.myorg.dto.response.security.ProfileFilterResponse;
import com.myorg.dto.response.security.ProfileResponse;
import com.myorg.dto.response.security.UserFilterResponse;
import com.myorg.dto.response.security.UserFindAllResponse;
import com.myorg.dto.response.security.UserResponse;

public interface CovidAnalyticsService {

    //Auth
    LoginResponse authenticateUser(LoginRequest request);

    //Processes
    CovidHeaderFilterResponse findAllHeaderFilter(CovidHeaderFilterRequest request);

    CountResponse countAllHeaderFilter(CovidHeaderFilterRequest request);

    CovidLoadResponse getCovidLoad(Long id);

    CovidLoadResponse deleteCovidLoad(Long id);

    CovidLoadResponse postCovidLoad(CovidLoadRequest request, byte[] file);

    CovidDetailFilterResponse findAllDetailFilter(CovidDetailFilterRequest request);

    CountResponse countAllDetailFilter(CovidDetailFilterRequest request);

    //Security
    ProfileResponse postProfile(ProfileRequest request);
    ProfileFilterResponse filterProfiles(ProfileFilterRequest request);
    CountResponse countProfilesFilter(ProfileFilterRequest request);
    ProfileResponse deleteProfile(Long id);
    ProfileResponse getProfile(Long id);

    UserResponse postUser(UserRequest request);
    UserFilterResponse filterUsers(UserFilterRequest request);
    CountResponse countUsersFilter(UserFilterRequest request);
    UserResponse deleteUser(Long id);
    UserResponse getUser(Long id);
    UserResponse getUserByUsernameOrMail(String username);

    //Settings
    UserSettingResponse postUserSetting(UserSettingRequest request);
    UserSettingResponse getRequestUserSetting();

    //Configurations
    CountryFilterResponse filterCountries(CountryFilterRequest request);
    CountResponse countCountriesFilter(CountryFilterRequest request);
    CountryResponse deleteCountry(Long id);
    CountryResponse getCountry(Long id);
    CountryResponse postCountry(CountryRequest request);

    //Dashboard
    DashboardOneResponse getBoardOneData();
    DashboardTwoResponse getBoardTwoData(DashboardTwoFilterRequest request);

    //Data Fetch
    PermitFetchResponse getPermits();
    UserFindAllResponse getUsers();
    CountryFindAllResponse getCountries();
    ProfileFetchResponse getProfiles();

}
