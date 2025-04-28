package com.achievix.kafka;

import com.achievix.sendgrid.EmailService;
import com.achievix.sendgrid.dto.EmailNotificationDTO;
import com.achievix.task.dto.TaskCompletedEventDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);
    private final EmailService emailService;

    public KafkaConsumerService(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "email-notifications", groupId = "achievix-group")
    public void consumeEmailNotification(EmailNotificationDTO emailNotification) {
        logger.info("Received email notification: {}", emailNotification);
        emailService.sendEmail(emailNotification);
    }

    @KafkaListener(topics = "task-completed", groupId = "achievix-group")
    public void consumeTaskCompletedEvent(TaskCompletedEventDTO taskCompletedEvent) {
        logger.info("Received task completed event: Task ID={}, Title={}, Goal ID={}, Completed At={}",
                taskCompletedEvent.getTaskId(),
                taskCompletedEvent.getTitle(),
                taskCompletedEvent.getGoalId(),
                taskCompletedEvent.getCompletedAt());
    }
}
