package com.academic.bff.config;

import com.academic.bff.proxy.BffProxy;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebController {

    private final BffProxy bffProxy;
    private final ObjectMapper objectMapper = new ObjectMapper();


    @GetMapping("/")
    public String root() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            Model model) {
        try {
            String body = String.format(
                    "{\"username\":\"%s\",\"password\":\"%s\"}",
                    username, password);

            var response = bffProxy.forwardToAuth(
                    "POST", "/api/auth/login", body);

            if (response.getStatusCode().is2xxSuccessful()) {
                JsonNode json = objectMapper.readTree(response.getBody());

                String token = json.get("token").asText();
                String userId = json.get("userId").asText();
                String role = json.get("role").asText();

                session.setAttribute("token", token);
                session.setAttribute("userId", userId);
                session.setAttribute("role", role);
                session.setAttribute("username", username);

                // ← ADD HERE
                log.info("Session set — userId: {}, role: {}, token: {}",
                        userId,
                        role,
                        token != null ? "SET" : "NULL");

                return "redirect:/dashboard";
            } else {
                model.addAttribute("error", "Invalid credentials");
                return "login";
            }
        } catch (Exception e) {
            log.error("Login error: {}", e.getMessage());
            model.addAttribute("error", "Login failed. Please try again.");
            return "login";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (session.getAttribute("token") == null) {
            return "redirect:/login";
        }
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("role", session.getAttribute("role"));
        model.addAttribute("userId", session.getAttribute("userId")); // ← ADD THIS!
        return "dashboard";
    }

    @GetMapping("/students")
    public String students(HttpSession session, Model model) {
        if (session.getAttribute("token") == null) {
            return "redirect:/login";
        }
        String token = (String) session.getAttribute("token");
        String teacherId = (String) session.getAttribute("userId");

        model.addAttribute("userId", teacherId);
        model.addAttribute("role", session.getAttribute("role"));

        try {
            var response = bffProxy.forwardWithToken(
                    "GET",
                    "/api/students/teacher/" + teacherId,
                    null, token);
            model.addAttribute("studentsJson", response.getBody());
        } catch (Exception e) {
            model.addAttribute("error", "Could not load students");
            model.addAttribute("studentsJson", "[]");
        }
        return "students";
    }

    @GetMapping("/attendance")
    public String attendance(HttpSession session, Model model) {
        if (session.getAttribute("token") == null) {
            return "redirect:/login";
        }
        model.addAttribute("role", session.getAttribute("role"));
        model.addAttribute("userId", session.getAttribute("userId"));
        model.addAttribute("username", session.getAttribute("username"));
        return "attendance";
    }

    @GetMapping("/assignments")
    public String assignments(HttpSession session, Model model) {
        if (session.getAttribute("token") == null) {
            return "redirect:/login";
        }
        String token = (String) session.getAttribute("token");
        String userId = (String) session.getAttribute("userId");
        String role = (String) session.getAttribute("role");

        try {
            String path = role.equals("TEACHER")
                    ? "/api/assignments/teacher/" + userId
                    : "/api/assignments/student/" + userId;

            var response = bffProxy.forwardWithToken("GET", path, null, token);
            if (response.getStatusCode().is2xxSuccessful()) {
                model.addAttribute("assignmentsJson", response.getBody());
            } else {
                model.addAttribute("assignmentsJson", "[]");
            }
        } catch (Exception e) {
            log.error("Error fetching assignments: {}", e.getMessage());
            model.addAttribute("assignmentsJson", "[]");
        }

        model.addAttribute("role", role);
        model.addAttribute("userId", userId);
        model.addAttribute("username", session.getAttribute("username"));
        return "assignments";
    }

    @GetMapping("/assignments/{assignmentId}")
    public String assignmentDetail(
            @PathVariable String assignmentId,
            HttpSession session,
            Model model) {
        if (session.getAttribute("token") == null) {
            return "redirect:/login";
        }
        String token = (String) session.getAttribute("token");

        try {
            var response = bffProxy.forwardWithToken(
                    "GET", "/api/assignments/" + assignmentId, null, token);
            model.addAttribute("assignmentJson", response.getBody());
        } catch (Exception e) {
            model.addAttribute("error", "Could not load assignment");
        }

        model.addAttribute("role", session.getAttribute("role"));
        model.addAttribute("userId", session.getAttribute("userId"));
        model.addAttribute("username", session.getAttribute("username"));
        return "assignment-detail";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    private String extractField(String json, String field) {
        try {
            String search = "\"" + field + "\":\"";
            int start = json.indexOf(search) + search.length();
            int end = json.indexOf("\"", start);
            return json.substring(start, end);
        } catch (Exception e) {
            return "";
        }
    }
}