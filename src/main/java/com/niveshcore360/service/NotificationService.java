package com.niveshcore360.service;

import com.niveshcore360.entity.Notification;
import java.util.List;

/**
 * Service interface for querying notifications and updating read state.
 */
public interface NotificationService {
    List<Notification> getNotificationsForUser(Long userId);
    List<Notification> getUnreadNotificationsForUser(Long userId);
    void markAsRead(Long notificationId);
    void createNotification(Long userId, String message);
}
