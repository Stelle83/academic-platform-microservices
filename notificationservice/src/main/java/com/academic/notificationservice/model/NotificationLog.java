package com.academic.notificationservice.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String recipientId;

    @Column(nullable = false)
    private String recipientEmail;

    @Column(nullable = false)
    private String subject;

    @Column(length = 2000)
    private String message;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Column(nullable = false)
    private LocalDateTime sentAt;

    @Column(nullable = false)
    private boolean success;

    public enum NotificationType {
        ASSIGNMENT_REMINDER,
        ASSIGNMENT_GRADED,
        ATTENDANCE_WARNING
    }
}