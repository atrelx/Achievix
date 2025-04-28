package com.achievix.sendgrid.dto;

import lombok.Data;

@Data
public class EmailNotificationDTO {
    private String toEmail;
    private String subject;
    private String body;
}