package com.myorg.service.impl;

import com.myorg.config.AppInfo;
import com.myorg.dto.ResponseInfo;
import com.myorg.dto.ResponseResults;
import com.myorg.dto.response.security.LoginResponse;
import com.myorg.dto.response.security.LoginResponseData;
import com.myorg.dto.security.LoginRequest;
import com.myorg.dto.security.PermitResponse;
import com.myorg.service.CovidAnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
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

    //process
    //    private final String  AUTH_ENDPOINT = "/auth";
    //    private final String  LOGIN_ENDPOINT = "/login";

    private final HttpClient client = HttpClient.newHttpClient();

    private HttpRequest.Builder builderPOST(String endpoint, String body) {
        return HttpRequest.newBuilder()
                .uri(URI.create(appInfo.getCovidAnalyticsApi() + API_VERSION + endpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body));
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

    //Security
    public PermitResponse getPermits() {
        return null;
    }

    private HttpRequest.BodyPublisher ofJsonAndFile(
            String json,
            Path file)
            throws Exception {

        String boundary = UUID.randomUUID().toString();
        String jsonFieldName = "metadata";
        List<byte[]> parts = new ArrayList<>();

        // ---- JSON part
        parts.add(("--" + boundary + "\r\n").getBytes(StandardCharsets.UTF_8));
        parts.add(("Content-Disposition: form-data;"
                + "name=\"" + jsonFieldName + "\"\r\n"
                + "Content-Type: application/json\r\n\r\n").getBytes(
                StandardCharsets.UTF_8));
        parts.add(json.getBytes(StandardCharsets.UTF_8));
        parts.add("\r\n".getBytes(StandardCharsets.UTF_8));

        // ---- File part
        String fileContentType = "text/csv";
        String fileFieldName = System.currentTimeMillis() + "";

        parts.add(("--" + boundary + "\r\n").getBytes(StandardCharsets.UTF_8));
        parts.add(("Content-Disposition: form-data; name=\"" + fileFieldName
                + "\"; filename=\"" + file.getFileName() + "\"\r\n" + "Content-Type: "
                + fileContentType + "\r\n\r\n").getBytes(StandardCharsets.UTF_8));
        parts.add(Files.readAllBytes(file));
        parts.add("\r\n".getBytes(StandardCharsets.UTF_8));

        // ---- Closing boundary
        parts.add(("--" + boundary + "--").getBytes(StandardCharsets.UTF_8));

        return HttpRequest.BodyPublishers.ofByteArrays(parts);
    }
}
