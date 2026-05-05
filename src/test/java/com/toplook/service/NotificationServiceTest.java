package com.toplook.service;

import com.toplook.service.notification.NotificationStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationStrategy logNotification;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void sendLikeNotification_ShouldCallStrategy() {
        notificationService.sendLikeNotification("alice", "bob");
        verify(logNotification).notify("alice", "bob liked your post!");
    }

    @Test
    void sendCommentNotification_ShouldCallStrategy() {
        notificationService.sendCommentNotification("alice", "bob");
        verify(logNotification).notify("alice", "bob commented on your post!");
    }

    @Test
    void sendFollowNotification_ShouldCallStrategy() {
        notificationService.sendFollowNotification("alice", "bob");
        verify(logNotification).notify("alice", "bob started following you!");
    }
}
