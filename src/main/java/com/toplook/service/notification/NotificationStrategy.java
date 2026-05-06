package com.toplook.service.notification;

public interface NotificationStrategy {
    void notify(String recipient, String message);
}
