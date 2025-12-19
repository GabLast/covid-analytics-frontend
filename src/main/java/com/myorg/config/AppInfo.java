package com.myorg.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
//enables the injection of properties from application.properties
@EnableConfigurationProperties
@ConfigurationPropertiesScan(basePackages = "com.integrator.application")
public class AppInfo {

    @Value("${app.name}")
    private String appName;

    @Value("${app.version}")
    private String appVersion;

    @Value("${spring.profiles.active:dev}")
    private String profile;

    @Value("${covid.analytics.api:http://127.0.0.1:8080/api}")
    private String covidAnalyticsApi;

}
