package com.academic.bff.config;

import com.academic.bff.proxy.BffProxy;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class BffController {

    private final BffProxy bffProxy;

    // ── Auth (public) ──────────────────────────────────────
    @PostMapping("/api/auth/**")
    public ResponseEntity<String> authPost(
            HttpServletRequest request,
            @RequestBody(required = false) String body) {
        return bffProxy.forwardToAuth("POST", getPath(request), body);
    }

    // ── Register a Student ───────────────────────────────────────────
    @PostMapping("/api/auth/register")
    public ResponseEntity<String> authRegisterPost(
            HttpServletRequest request,
            @RequestBody(required = false) String body) {
        return bffProxy.forwardToAuth("POST", "/api/auth/register", body);
    }

    // ── Students ───────────────────────────────────────────
    @PostMapping("/api/students/**")
    public ResponseEntity<String> studentPost(
            HttpServletRequest request,
            @RequestBody(required = false) String body) {
        return bffProxy.forwardToStudent("POST", getPath(request), body, getUserId(request));
    }

    @GetMapping("/api/students/**")
    public ResponseEntity<String> studentGet(HttpServletRequest request) {
        return bffProxy.forwardToStudent("GET", getPath(request), null, getUserId(request));
    }

    @PutMapping("/api/students/**")
    public ResponseEntity<String> studentPut(
            HttpServletRequest request,
            @RequestBody(required = false) String body) {
        return bffProxy.forwardToStudent("PUT", getPath(request), body, getUserId(request));
    }

    @DeleteMapping("/api/students/**")
    public ResponseEntity<String> studentDelete(HttpServletRequest request) {
        return bffProxy.forwardToStudent("DELETE", getPath(request), null, getUserId(request));
    }

    // ── Attendance ─────────────────────────────────────────
    @PostMapping("/api/attendance/**")
    public ResponseEntity<String> attendancePost(
            HttpServletRequest request,
            @RequestBody(required = false) String body) {
        return bffProxy.forwardToAttendance("POST", getPath(request), body, getUserId(request));
    }

    @GetMapping("/api/attendance/**")
    public ResponseEntity<String> attendanceGet(HttpServletRequest request) {
        return bffProxy.forwardToAttendance("GET", getPath(request), null, getUserId(request));
    }

    // ── Assignments ────────────────────────────────────────
    @PostMapping("/api/assignments/**")
    public ResponseEntity<String> assignmentPost(
            HttpServletRequest request,
            @RequestBody(required = false) String body) {
        return bffProxy.forwardToAssignment("POST", getPath(request), body, getUserId(request));
    }

    @GetMapping("/api/assignments/**")
    public ResponseEntity<String> assignmentGet(HttpServletRequest request) {
        return bffProxy.forwardToAssignment("GET", getPath(request), null, getUserId(request));
    }

    @PutMapping("/api/assignments/**")
    public ResponseEntity<String> assignmentPut(
            HttpServletRequest request,
            @RequestBody(required = false) String body) {
        return bffProxy.forwardToAssignment("PUT", getPath(request), body, getUserId(request));
    }

    @GetMapping("/api/session/token")
    @ResponseBody
    public ResponseEntity<String> getSessionToken(HttpSession session) {
        String token = (String) session.getAttribute("token");
        if (token == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok("{\"token\":\"" + token + "\"}");
    }

    // ── Helpers ────────────────────────────────────────────
    private String getPath(HttpServletRequest request) {
        String path = request.getRequestURI();
        String query = request.getQueryString();
        return query != null ? path + "?" + query : path;
    }

    private String getUserId(HttpServletRequest request) {
        return (String) request.getAttribute("userId");
    }
}