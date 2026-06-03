package com.academic.notificationservice.repository;

import com.academic.notificationservice.model.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationLogRepository extends JpaRepository<NotificationLog, String> {
    List<NotificationLog> findByRecipientId(String recipientId);
    List<NotificationLog> findByType(NotificationLog.NotificationType type);
    List<NotificationLog> findByRecipientIdOrderBySentAtDesc(String recipientId);
    List<NotificationLog> findTop10ByOrderBySentAtDesc();
}