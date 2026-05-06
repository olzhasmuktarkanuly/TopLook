package com.toplook.service.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("logNotification")
public class LogNotificationStrategy implements NotificationStrategy {

    private static final Logger log = LoggerFactory.getLogger(LogNotificationStrategy.class);

    @Override
    public void notify(String recipient, String message) {
        log.info("[NOTIFICATION] To: {} | Message: {}", recipient, message);
    }
}
