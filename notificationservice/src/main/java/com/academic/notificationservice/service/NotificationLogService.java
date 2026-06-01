package com.academic.notificationservice.service;

import com.academic.notificationservice.model.NotificationLog;
import com.academic.notificationservice.model.NotificationLog.NotificationType;
import com.academic.notificationservice.repository.NotificationLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationLogService {

    private final NotificationLogRepository repository;

    public void log(String recipientId,
                    String recipientEmail,
                    String subject,
                    String message,
                    NotificationType type,
                    boolean success) {
        NotificationLog logEntry = NotificationLog.builder()
                .recipientId(recipientId)
                .recipientEmail(recipientEmail)
                .subject(subject)
                .message(message)
                .type(type)
                .sentAt(LocalDateTime.now())
                .success(success)
                .build();
        repository.save(logEntry);
        log.info("Notification logged: {} → {}", type, recipientEmail);
    }
}