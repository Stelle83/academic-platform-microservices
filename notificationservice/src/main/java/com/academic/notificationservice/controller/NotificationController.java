package com.academic.notificationservice.controller;

import com.academic.notificationservice.model.NotificationLog;
import com.academic.notificationservice.repository.NotificationLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationLogRepository repository;

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<NotificationLog>> getStudentNotifications(
            @PathVariable String studentId) {
        return ResponseEntity.ok(
                repository.findByRecipientIdOrderBySentAtDesc(studentId));
    }

    @GetMapping("/recent")
    public ResponseEntity<List<NotificationLog>> getRecent() {
        return ResponseEntity.ok(
                repository.findTop10ByOrderBySentAtDesc());
    }
}