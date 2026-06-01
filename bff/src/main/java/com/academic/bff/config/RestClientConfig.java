package com.academic.bff.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${services.auth-url}")
    private String authUrl;

    @Value("${services.student-url}")
    private String studentUrl;

    @Value("${services.attendance-url}")
    private String attendanceUrl;

    @Value("${services.assignment-url}")
    private String assignmentUrl;

    @Bean("authClient")
    public RestClient authClient() {
        return RestClient.builder()
                .baseUrl(authUrl)
                .build();
    }

    @Bean("studentClient")
    public RestClient studentClient() {
        return RestClient.builder()
                .baseUrl(studentUrl)
                .build();
    }

    @Bean("attendanceClient")
    public RestClient attendanceClient() {
        return RestClient.builder()
                .baseUrl(attendanceUrl)
                .build();
    }

    @Bean("assignmentClient")
    public RestClient assignmentClient() {
        return RestClient.builder()
                .baseUrl(assignmentUrl)
                .build();
    }
}