package com.academic.notificationservice.consumer;

import com.academic.notificationservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final EmailService emailService;

    @RabbitListener(queues = "assignment-created")
    public void handleAssignmentCreated(Map<String, Object> event) {
        log.info("Received assignment-created event: {}", event);
        String studentId = (String) event.get("studentId");
        String title = (String) event.get("title");
        String deadline = (String) event.get("deadline");

        // In real system we'd look up student email via gRPC
        // For now we use a placeholder email
        String studentEmail = studentId + "@student.academic.se";

        emailService.sendAssignmentReminder(studentEmail, studentId, title, deadline);
    }

    @RabbitListener(queues = "assignment-graded")
    public void handleAssignmentGraded(Map<String, Object> event) {
        log.info("Received assignment-graded event: {}", event);
        String studentId = (String) event.get("studentId");
        String grade = event.get("grade").toString();
        String feedbackCategory = (String) event.get("feedbackCategory");

        String studentEmail = studentId + "@student.academic.se";

        emailService.sendAssignmentGraded(
                studentEmail,
                studentId,
                "Your assignment",
                Integer.parseInt(grade),
                feedbackCategory
        );
    }

    @RabbitListener(queues = "attendance-warning")
    public void handleAttendanceWarning(Map<String, Object> event) {
        log.info("Received attendance-warning event: {}", event);
        String studentId = (String) event.get("studentId");
        long absenceCount = Long.parseLong(event.get("absenceCount").toString());

        String studentEmail = studentId + "@student.academic.se";

        emailService.sendAttendanceWarning(studentEmail, studentId, absenceCount);
    }
}