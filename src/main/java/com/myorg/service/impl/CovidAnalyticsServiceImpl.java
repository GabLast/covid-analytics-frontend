package com.myorg.service.impl;

import com.myorg.config.AppInfo;
import com.myorg.config.security.MyVaadinSession;
import com.myorg.dto.request.process.CovidDetailFilterRequest;
import com.myorg.dto.request.process.CovidHeaderFilterRequest;
import com.myorg.dto.request.process.CovidLoadRequest;
import com.myorg.dto.response.CountResponse;
import com.myorg.dto.response.configuration.CountryFindAllResponse;
import com.myorg.dto.response.dashboard.DashboardOneResponse;
import com.myorg.dto.response.process.CovidDetailFilterResponse;
import com.myorg.dto.response.process.CovidHeaderFilterResponse;
import com.myorg.dto.response.process.CovidLoadResponse;
import com.myorg.dto.response.security.LoginResponse;
import com.myorg.dto.response.security.UserFindAllResponse;
import com.myorg.dto.request.security.LoginRequest;
import com.myorg.dto.response.security.PermitResponse;
import com.myorg.encapsulations.User;
import com.myorg.encapsulations.UserSetting;
import com.myorg.service.CovidAnalyticsService;
import com.myorg.utils.DateUtilities;
import com.vaadin.flow.server.VaadinSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CovidAnalyticsServiceImpl implements CovidAnalyticsService {

    private final ObjectMapper objectMapper;

    private final AppInfo appInfo;
    private final String  API_VERSION = "/v1";

    //auth
    private final String AUTH_ENDPOINT  = "/auth";
    private final String LOGIN_ENDPOINT = "/login";

    //processes
    private final String LOAD_ENDPOINT      = "/load";
    private final String DETAIL_ENDPOINT    = "/details";
    private final String FIND_ALL_ENDPOINT  = "/findall";
    private final String COUNT_ALL_ENDPOINT = "/countall";

    //data fetch
    private final String USER_ENDPOINT    = "/user";
    private final String PERMIT_ENDPOINT  = "/permits";
    private final String COUNTRY_ENDPOINT = "/country";

    //dashboard
    private final String DASHBOARD_ENDPOINT = "/dashboard";
    private final String DASHBOARD_ONE_ENDPOINT = "/one";
    private final String DASHBOARD_TWO_ENDPOINT = "/two";

    private static final String CRLF = "\r\n";

    private final HttpClient client = HttpClient.newHttpClient();

    private String getToken() {
        User user = (User) VaadinSession.getCurrent()
                .getAttribute(MyVaadinSession.SessionVariables.USER.toString());
        if (user == null) {
            return "";
        }

        return user.token();
    }

    private UserSetting getUserSetting() {

        UserSetting setting = (UserSetting) VaadinSession.getCurrent()
                .getAttribute(MyVaadinSession.SessionVariables.USERSETTINGS.toString());

        if (setting == null) {
            return UserSetting.builder().darkMode(false).language("en")
                    .dateFormat("dd/MM/yyyy").build();
        }

        return setting;
    }

    private HttpRequest.Builder builderGET(String endpoint, Map<String, String> params) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(
                appInfo.getCovidAnalyticsApi() + API_VERSION + endpoint);

        if (params != null) {
            for (String key : params.keySet()) {
                if (params.get(key) != null && !StringUtils.isBlank(params.get(key))
                        && !params.get(key).equalsIgnoreCase("null")) {
                    builder.queryParam(key, params.get(key));
                }
            }
        }

        return HttpRequest.newBuilder().uri(builder.build().toUri())
                .header("Authorization", "Bearer " + getToken()).GET();
    }

    private HttpRequest.Builder builderDELETE(String endpoint,
            Map<String, String> params) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(
                appInfo.getCovidAnalyticsApi() + API_VERSION + endpoint);

        for (String key : params.keySet()) {
            builder.queryParam(key, params.get(key));
        }

        return HttpRequest.newBuilder().uri(builder.build().toUri())
                .header("Authorization", "Bearer " + getToken()).DELETE();
    }

    private HttpRequest.Builder builderPOST(String endpoint, String body) {
        return HttpRequest.newBuilder()
                .uri(URI.create(appInfo.getCovidAnalyticsApi() + API_VERSION + endpoint))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + getToken())
                .POST(HttpRequest.BodyPublishers.ofString(body));
    }

    private HttpRequest.Builder builderMultiFormPartPOST(String endpoint,
            HttpRequest.BodyPublisher body, String boundary) {
        return HttpRequest.newBuilder()
                .uri(URI.create(appInfo.getCovidAnalyticsApi() + API_VERSION + endpoint))
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .header("Authorization", "Bearer " + getToken()).POST(body);
    }

    //Auth
    public LoginResponse authenticateUser(LoginRequest request) {

        HttpResponse<String> response;
        try {

            HttpRequest req = builderPOST(AUTH_ENDPOINT + LOGIN_ENDPOINT,
                    objectMapper.writeValueAsString(request)).build();

            response = client.send(req, HttpResponse.BodyHandlers.ofString());

            return objectMapper.readValue(response.body(), LoginResponse.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    //Processes
    @Override
    public CovidHeaderFilterResponse findAllHeaderFilter(
            CovidHeaderFilterRequest request) {
        HttpResponse<String> response;

        try {

            Map<String, String> params = new HashMap<>();
            params.put("enabled", String.valueOf(request.isEnabled()));
            params.put("userId", String.valueOf(request.getUserId()));
            params.put("description", request.getDescription());
            params.put("dateStart",
                    DateUtilities.getLocalDateAsString(request.getDateStart(), null));
            params.put("dateEnd",
                    DateUtilities.getLocalDateAsString(request.getDateEnd(), null));

            params.put("offset", String.valueOf(request.getOffset()));
            params.put("limit", String.valueOf(request.getLimit()));
            params.put("sortOrder", String.valueOf(request.getSortOrder()));
            params.put("sortProperty", String.valueOf(request.getSortProperty()));

            HttpRequest req =
                    builderGET(LOAD_ENDPOINT + FIND_ALL_ENDPOINT, params).build();

            response = client.send(req, HttpResponse.BodyHandlers.ofString());

            return objectMapper.readValue(response.body(),
                    CovidHeaderFilterResponse.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    @Override
    public CountResponse countAllHeaderFilter(CovidHeaderFilterRequest request) {
        HttpResponse<String> response;

        try {

            Map<String, String> params = new HashMap<>();
            params.put("enabled", String.valueOf(request.isEnabled()));
            params.put("userId", String.valueOf(request.getUserId()));
            params.put("description", request.getDescription());
            params.put("dateStart",
                    DateUtilities.getLocalDateAsString(request.getDateStart(), null));
            params.put("dateEnd",
                    DateUtilities.getLocalDateAsString(request.getDateEnd(), null));
            HttpRequest req =
                    builderGET(LOAD_ENDPOINT + COUNT_ALL_ENDPOINT, params).build();

            response = client.send(req, HttpResponse.BodyHandlers.ofString());
            return objectMapper.readValue(response.body(), CountResponse.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    @Override
    public CovidDetailFilterResponse findAllDetailFilter(
            CovidDetailFilterRequest request) {
        HttpResponse<String> response;

        try {

            Map<String, String> params = new HashMap<>();
            params.put("enabled", String.valueOf(request.isEnabled()));
            params.put("headerId", String.valueOf(request.getHeaderId()));
            params.put("country", request.getCountry());
            params.put("dateStart",
                    DateUtilities.getLocalDateAsString(request.getDateStart(), null));
            params.put("dateEnd",
                    DateUtilities.getLocalDateAsString(request.getDateEnd(), null));

            params.put("offset", String.valueOf(request.getOffset()));
            params.put("limit", String.valueOf(request.getLimit()));
            params.put("sortOrder", String.valueOf(request.getSortOrder()));
            params.put("sortProperty", String.valueOf(request.getSortProperty()));

            HttpRequest req =
                    builderGET(LOAD_ENDPOINT + DETAIL_ENDPOINT + FIND_ALL_ENDPOINT,
                            params).build();

            response = client.send(req, HttpResponse.BodyHandlers.ofString());

            return objectMapper.readValue(response.body(),
                    CovidDetailFilterResponse.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    @Override
    public CountResponse countAllDetailFilter(CovidDetailFilterRequest request) {
        HttpResponse<String> response;

        try {

            Map<String, String> params = new HashMap<>();
            params.put("enabled", String.valueOf(request.isEnabled()));
            params.put("headerId", String.valueOf(request.getHeaderId()));
            params.put("country", request.getCountry());
            params.put("dateStart",
                    DateUtilities.getLocalDateAsString(request.getDateStart(), null));
            params.put("dateEnd",
                    DateUtilities.getLocalDateAsString(request.getDateEnd(), null));

            HttpRequest req =
                    builderGET(LOAD_ENDPOINT + DETAIL_ENDPOINT + COUNT_ALL_ENDPOINT,
                            params).build();

            response = client.send(req, HttpResponse.BodyHandlers.ofString());

            return objectMapper.readValue(response.body(), CountResponse.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    //Dashboard
    @Override
    public DashboardOneResponse getBoardOneData() {
        HttpResponse<String> response;

        try {
            HttpRequest req = builderGET(LOAD_ENDPOINT, null).build();
            response = client.send(req, HttpResponse.BodyHandlers.ofString());
            return objectMapper.readValue(response.body(), DashboardOneResponse.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    @Override
    public CovidLoadResponse getHeaderData(Long id) {
        HttpResponse<String> response;

        try {
            Map<String, String> params = new HashMap<>();
            params.put("id", String.valueOf(id));

            HttpRequest req = builderGET(LOAD_ENDPOINT, params).build();
            response = client.send(req, HttpResponse.BodyHandlers.ofString());
            return objectMapper.readValue(response.body(), CovidLoadResponse.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    @Override
    public CovidLoadResponse deleteHeader(Long id) {
        HttpResponse<String> response;

        try {

            Map<String, String> params = new HashMap<>();
            params.put("id", String.valueOf(id));

            HttpRequest req = builderDELETE(LOAD_ENDPOINT, params).build();

            response = client.send(req, HttpResponse.BodyHandlers.ofString());
            return objectMapper.readValue(response.body(), CovidLoadResponse.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    @Override
    public CovidLoadResponse loadData(CovidLoadRequest request, byte[] file) {
        HttpResponse<String> response;
        try {
            String boundary = UUID.randomUUID().toString();

            HttpRequest req = builderMultiFormPartPOST(LOAD_ENDPOINT,
                    generateMultiPartForm(objectMapper.writeValueAsString(request), file,
                            boundary), boundary).build();

            response = client.send(req, HttpResponse.BodyHandlers.ofString());

            return objectMapper.readValue(response.body(), CovidLoadResponse.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    private HttpRequest.BodyPublisher generateMultiPartForm(String json, byte[] file,
            String boundary) {

        List<byte[]> parts = new ArrayList<>();

        // --- JSON part ---
        parts.add(("--" + boundary + CRLF
                + "Content-Disposition: form-data; name=\"metadata\"" + CRLF
                + "Content-Type: application/json" + CRLF + CRLF + json + CRLF).getBytes(
                StandardCharsets.UTF_8));

        // --- File part ---
        if (file != null) {
            String fileName = System.currentTimeMillis() + "";
            String contentType = "text/csv";

            parts.add(("--" + boundary + CRLF
                    + "Content-Disposition: form-data; name=\"file\"; filename=\""
                    + fileName + "\"" + CRLF + "Content-Type: " + contentType + CRLF
                    + CRLF).getBytes(StandardCharsets.UTF_8));

            parts.add(file);
            parts.add(CRLF.getBytes(StandardCharsets.UTF_8));
        }

        // --- End boundary ---
        parts.add(("--" + boundary + "--" + CRLF).getBytes(StandardCharsets.UTF_8));

        return HttpRequest.BodyPublishers.ofByteArrays(parts);

    }

    //Data Fetch
    @Override
    public PermitResponse getPermits() {
        HttpResponse<String> response;

        try {
            HttpRequest req = builderGET(PERMIT_ENDPOINT, null).build();
            response = client.send(req, HttpResponse.BodyHandlers.ofString());
            return objectMapper.readValue(response.body(), PermitResponse.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    @Override
    public UserFindAllResponse getUsers() {
        HttpResponse<String> response;

        try {
            HttpRequest req = builderGET(USER_ENDPOINT + FIND_ALL_ENDPOINT, null).build();
            response = client.send(req, HttpResponse.BodyHandlers.ofString());
            return objectMapper.readValue(response.body(), UserFindAllResponse.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    @Override
    public CountryFindAllResponse getCountries() {
        HttpResponse<String> response;
        try {
            HttpRequest req =
                    builderGET(COUNTRY_ENDPOINT + FIND_ALL_ENDPOINT, null).build();
            response = client.send(req, HttpResponse.BodyHandlers.ofString());
            return objectMapper.readValue(response.body(), CountryFindAllResponse.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

}
