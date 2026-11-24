package com.cebuguide.backend.service;

import com.cebuguide.backend.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/** Sends the password reset email through the configured SMTP account (Gmail by default). */
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final ObjectProvider<JavaMailSender> mailSender;
    private final String smtpUsername;
    private final String from;
    private final String frontendUrl;
    private final long tokenMinutes;

    public EmailService(ObjectProvider<JavaMailSender> mailSender,
                        @Value("${spring.mail.username:}") String smtpUsername,
                        @Value("${app.mail.from:}") String from,
                        @Value("${app.frontend-url}") String frontendUrl,
                        @Value("${app.reset-token-minutes}") long tokenMinutes) {
        this.mailSender = mailSender;
        this.smtpUsername = smtpUsername;
        this.from = from;
        this.frontendUrl = frontendUrl;
        this.tokenMinutes = tokenMinutes;
    }

    public void sendPasswordReset(User user, String rawToken) {
        String link = frontendUrl + "/reset-password?token=" + URLEncoder.encode(rawToken, StandardCharsets.UTF_8);
        JavaMailSender sender = mailSender.getIfAvailable();

        if (sender == null || smtpUsername.isBlank()) {
            // Development fallback only: lets the flow be tested before email is configured.
            log.warn("Email is not configured (MAIL_USERNAME / MAIL_PASSWORD). Reset link for {}: {}", user.getEmail(), link);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from.isBlank() ? smtpUsername : from);
            message.setTo(user.getEmail());
            message.setSubject("CebuGuide password reset");
            message.setText("Hello " + user.getName() + ",\n\n"
                    + "We received a request to reset your CebuGuide password.\n"
                    + "Open the link below to choose a new password. The link works once and expires in "
                    + tokenMinutes + " minutes.\n\n"
                    + link + "\n\n"
                    + "If you did not request this, you can ignore this email. Your password will not change.\n");
            sender.send(message);
            log.info("Password reset email sent to {}", user.getEmail());
        } catch (Exception e) {
            // The caller always shows the same message, so a delivery problem must not reveal anything to the visitor.
            log.error("Could not send the password reset email to {}: {}", user.getEmail(), e.getMessage());
        }
    }
}
