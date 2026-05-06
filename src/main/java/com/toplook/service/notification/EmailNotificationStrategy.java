package com.toplook.service.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("emailNotification")
public class EmailNotificationStrategy implements NotificationStrategy {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationStrategy.class);

    @Override
    public void notify(String recipient, String message) {
        log.info("[EMAIL] Sending to: {} | Content: {}", recipient, message);
    }
}
