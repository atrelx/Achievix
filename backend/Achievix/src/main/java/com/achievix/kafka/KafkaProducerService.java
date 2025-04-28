package com.achievix.kafka;

import com.achievix.sendgrid.dto.EmailNotificationDTO;
import com.achievix.task.dto.TaskCompletedEventDTO;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEmailNotification(EmailNotificationDTO emailNotification) {
        kafkaTemplate.send("email-notifications", emailNotification.getToEmail(), emailNotification);
    }

    public void sendTaskCompletedEvent(TaskCompletedEventDTO taskCompletedEvent) {
        kafkaTemplate.send("task-completed", String.valueOf(taskCompletedEvent.getTaskId()), taskCompletedEvent);
    }
}