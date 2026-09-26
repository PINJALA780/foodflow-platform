package com.foodflow.notification.service;

import com.foodflow.notification.dto.CreateNotificationRequest;
import com.foodflow.notification.dto.NotificationResponse;
import com.foodflow.notification.entity.Notification;
import com.foodflow.notification.entity.NotificationType;
import com.foodflow.notification.exception.ResourceNotFoundException;
import com.foodflow.notification.repository.NotificationRepository;
import com.foodflow.notification.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private UUID sampleId;
    private UUID sampleUserId;
    private Notification sampleNotification;

    @BeforeEach
    void setUp() {
        sampleId = UUID.randomUUID();
        sampleUserId = UUID.randomUUID();

        sampleNotification = new Notification();
        sampleNotification.setId(sampleId);
        sampleNotification.setUserId(sampleUserId);
        sampleNotification.setType(NotificationType.ORDER_PLACED);
        sampleNotification.setTitle("Order Placed");
        sampleNotification.setMessage("Your order has been placed successfully!");
        sampleNotification.setIsRead(false);
    }

    @Test
    void createNotification_Success() {
        CreateNotificationRequest req = new CreateNotificationRequest();
        req.setUserId(sampleUserId);
        req.setType(NotificationType.ORDER_PLACED);
        req.setTitle("Order Placed");
        req.setMessage("Your order has been placed successfully!");

        when(notificationRepository.save(any(Notification.class))).thenReturn(sampleNotification);

        NotificationResponse res = notificationService.createNotification(req);

        assertNotNull(res);
        assertEquals(sampleUserId, res.getUserId());
        assertEquals("Order Placed", res.getTitle());
        assertFalse(res.getIsRead());
    }

    @Test
    void getNotificationsByUser_Success() {
        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(sampleUserId)).thenReturn(List.of(sampleNotification));

        List<NotificationResponse> results = notificationService.getNotificationsByUser(sampleUserId);

        assertEquals(1, results.size());
        assertEquals("Order Placed", results.get(0).getTitle());
    }

    @Test
    void markAsRead_Success() {
        when(notificationRepository.findById(sampleId)).thenReturn(Optional.of(sampleNotification));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        NotificationResponse res = notificationService.markAsRead(sampleId);

        assertTrue(res.getIsRead());
    }

    @Test
    void deleteNotification_Success() {
        when(notificationRepository.findById(sampleId)).thenReturn(Optional.of(sampleNotification));
        doNothing().when(notificationRepository).delete(sampleNotification);

        assertDoesNotThrow(() -> notificationService.deleteNotification(sampleId));
        verify(notificationRepository, times(1)).delete(sampleNotification);
    }
}
