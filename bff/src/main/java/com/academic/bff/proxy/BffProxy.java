package com.academic.bff.proxy;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@Slf4j
public class BffProxy {

    private final RestClient authClient;
    private final RestClient studentClient;
    private final RestClient attendanceClient;
    private final RestClient assignmentClient;

    public BffProxy(
            @Qualifier("authClient") RestClient authClient,
            @Qualifier("studentClient") RestClient studentClient,
            @Qualifier("attendanceClient") RestClient attendanceClient,
            @Qualifier("assignmentClient") RestClient assignmentClient) {
        this.authClient = authClient;
        this.studentClient = studentClient;
        this.attendanceClient = attendanceClient;
        this.assignmentClient = assignmentClient;
    }

    public ResponseEntity<String> forwardWithToken(
            String method, String path, String body, String token) {
        log.info("Forwarding {} {} with token", method, path);

        RestClient client;
        if (path.startsWith("/api/students")) client = studentClient;
        else if (path.startsWith("/api/attendance")) client = attendanceClient;
        else if (path.startsWith("/api/assignments")) client = assignmentClient;
        else client = authClient;

        try {
            RestClient.RequestBodySpec requestSpec = client
                    .method(org.springframework.http.HttpMethod.valueOf(method))
                    .uri(path)
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token);

            if (body != null && !body.isEmpty()) {
                requestSpec.body(body);
            }

            return requestSpec
                    .retrieve()
                    .toEntity(String.class);

        } catch (Exception e) {
            log.error("Error forwarding: {}", e.getMessage());
            return ResponseEntity.status(502).body("{\"error\":\"Service unavailable\"}");
        }
    }

    public ResponseEntity<String> forwardToAuth(
            String method, String path, String body) {
        return forward(authClient, method, path, body, null);
    }

    public ResponseEntity<String> forwardToStudent(
            String method, String path, String body, String userId) {
        return forward(studentClient, method, path, body, userId);
    }

    public ResponseEntity<String> forwardToAttendance(
            String method, String path, String body, String userId) {
        return forward(attendanceClient, method, path, body, userId);
    }

    public ResponseEntity<String> forwardToAssignment(
            String method, String path, String body, String userId) {
        return forward(assignmentClient, method, path, body, userId);
    }

    private ResponseEntity<String> forward(
            RestClient client,
            String method,
            String path,
            String body,
            String userId) {

        log.info("Forwarding {} {} userId={}", method, path, userId);

        try {
            RestClient.RequestBodySpec requestSpec = client
                    .method(org.springframework.http.HttpMethod.valueOf(method))
                    .uri(path)
                    .header("Content-Type", "application/json");

            if (userId != null) {
                requestSpec.header("X-User-Id", userId);
            }

            if (body != null && !body.isEmpty()) {
                requestSpec.body(body);
            }

            return requestSpec
                    .retrieve()
                    .toEntity(String.class);

        } catch (Exception e) {
            log.error("Error forwarding to {}: {}", path, e.getMessage());
            return ResponseEntity.status(502).body("{\"error\":\"Service unavailable\"}");
        }
    }
}