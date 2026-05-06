package com.toplook.service;

import com.toplook.service.notification.NotificationStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationStrategy logNotification;

    public void sendLikeNotification(String postAuthor, String liker) {
        logNotification.notify(postAuthor, liker + " liked your post!");
    }

    public void sendCommentNotification(String postAuthor, String commenter) {
        logNotification.notify(postAuthor, commenter + " commented on your post!");
    }

    public void sendFollowNotification(String target, String follower) {
        logNotification.notify(target, follower + " started following you!");
    }
}
