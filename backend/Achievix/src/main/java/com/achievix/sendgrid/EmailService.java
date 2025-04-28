package com.achievix.sendgrid;

import com.achievix.sendgrid.dto.EmailNotificationDTO;
import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Value("${sendgrid.api-key}")
    private String sendGridApiKey;

    @Value("${sendgrid.from-email-address}")
    private String fromEmail;

    public void sendEmail(EmailNotificationDTO emailNotification) {

        SendGrid sg = new SendGrid(sendGridApiKey);
        Email from = new Email(fromEmail);
        String subject = emailNotification.getSubject();
        Email to = new Email(emailNotification.getToEmail());
        Content content = new Content("text/plain", emailNotification.getBody());
        Mail mail = new Mail(from, subject, to, content);

        try {
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);

            logger.info("Email sent to {} with status code {}", emailNotification.getToEmail(), response.getStatusCode());
        } catch (IOException ex) {
            logger.error("Failed to send email to {}: {}", emailNotification.getToEmail(), ex.getMessage());
        }
    }
}