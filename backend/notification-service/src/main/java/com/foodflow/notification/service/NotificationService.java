package com.foodflow.notification.service;

import com.foodflow.notification.dto.CreateNotificationRequest;
import com.foodflow.notification.dto.NotificationResponse;

import java.util.List;
import java.util.UUID;

public interface NotificationService {
    NotificationResponse createNotification(CreateNotificationRequest request);
    List<NotificationResponse> getNotificationsByUser(UUID userId);
    NotificationResponse markAsRead(UUID id);
    void deleteNotification(UUID id);
}
