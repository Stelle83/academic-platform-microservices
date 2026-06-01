package com.academic.notificationservice.service;

import com.academic.notificationservice.model.NotificationLog.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final NotificationLogService logService;

    @Value("${notification.mail.from}")
    private String fromEmail;

    public void sendAssignmentReminder(String studentEmail,
                                       String studentId,
                                       String title,
                                       String deadline) {
        String subject = "Reminder: Assignment due soon — " + title;
        String message = String.format(
                "Hello!\n\nThis is a reminder that your assignment '%s' is due on %s.\n\n" +
                        "Please make sure to submit it on time.\n\nBest regards,\nAcademic Platform",
                title, deadline);

        send(studentEmail, studentId, subject, message, NotificationType.ASSIGNMENT_REMINDER);
    }

    public void sendAssignmentGraded(String studentEmail,
                                     String studentId,
                                     String title,
                                     int grade,
                                     String feedbackCategory) {
        String subject = "Your assignment has been graded — " + title;
        String message = String.format(
                "Hello!\n\nYour assignment '%s' has been graded.\n\n" +
                        "Grade: %d/5\nFeedback category: %s\n\n" +
                        "Log in to the platform to see detailed feedback.\n\nBest regards,\nAcademic Platform",
                title, grade, feedbackCategory);

        send(studentEmail, studentId, subject, message, NotificationType.ASSIGNMENT_GRADED);
    }

    public void sendAttendanceWarning(String studentEmail,
                                      String studentId,
                                      long absenceCount) {
        String subject = "Attendance Warning — Action Required";
        String message = String.format(
                "Hello!\n\nYou have accumulated %d absences.\n\n" +
                        "Please contact your teacher as soon as possible.\n\n" +
                        "Best regards,\nAcademic Platform",
                absenceCount);

        send(studentEmail, studentId, subject, message, NotificationType.ATTENDANCE_WARNING);
    }

    private void send(String to,
                      String recipientId,
                      String subject,
                      String message,
                      NotificationType type) {
        boolean success = false;
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setFrom(fromEmail);
            mail.setTo(to);
            mail.setSubject(subject);
            mail.setText(message);
            mailSender.send(mail);
            success = true;
            log.info("Email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        } finally {
            logService.log(recipientId, to, subject, message, type, success);
        }
    }
}
