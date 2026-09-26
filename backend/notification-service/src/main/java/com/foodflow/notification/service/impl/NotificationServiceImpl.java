package com.foodflow.notification.service.impl;

import com.foodflow.notification.dto.CreateNotificationRequest;
import com.foodflow.notification.dto.NotificationResponse;
import com.foodflow.notification.entity.Notification;
import com.foodflow.notification.exception.ResourceNotFoundException;
import com.foodflow.notification.repository.NotificationRepository;
import com.foodflow.notification.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public NotificationResponse createNotification(CreateNotificationRequest request) {
        Notification notification = new Notification();
        notification.setUserId(request.getUserId());
        notification.setOrderId(request.getOrderId());
        notification.setType(request.getType());
        notification.setTitle(request.getTitle());
        notification.setMessage(request.getMessage());
        notification.setIsRead(false);

        return toResponse(notificationRepository.save(notification));
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsByUser(UUID userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public NotificationResponse markAsRead(UUID id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", id));

        notification.setIsRead(true);
        return toResponse(notificationRepository.save(notification));
    }

    @Override
    public void deleteNotification(UUID id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", id));

        notificationRepository.delete(notification);
    }

    private NotificationResponse toResponse(Notification notification) {
        NotificationResponse res = new NotificationResponse();
        res.setId(notification.getId());
        res.setUserId(notification.getUserId());
        res.setOrderId(notification.getOrderId());
        res.setType(notification.getType());
        res.setTitle(notification.getTitle());
        res.setMessage(notification.getMessage());
        res.setIsRead(notification.getIsRead());
        res.setCreatedAt(notification.getCreatedAt());
        return res;
    }
}
