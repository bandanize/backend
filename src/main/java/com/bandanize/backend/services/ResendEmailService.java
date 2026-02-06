package com.bandanize.backend.services;

import com.resend.Resend;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import com.resend.core.exception.ResendException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ResendEmailService implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(ResendEmailService.class);

    private final Resend resend;

    @Value("${resend.from.email}")
    private String fromEmail;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public ResendEmailService(@Value("${resend.api.key}") String apiKey) {
        if (apiKey == null || apiKey.isEmpty()) {
            logger.warn("Resend API key is missing. Email sending will fail.");
            this.resend = null;
        } else {
            this.resend = new Resend(apiKey);
        }
    }

    @Override
    public void sendPasswordReset(String to, String token) {
        if (resend == null) {
            logger.warn("Skipping email send: Resend client not initialized.");
            return;
        }

        String resetLink = frontendUrl + "/reset-password?token=" + token;

        String htmlContent = "<p>You requested a password reset.</p>" +
                "<p>Click the link below to reset your password:</p>" +
                "<a href=\"" + resetLink + "\">Reset Password</a>" +
                "<p>If you did not request this, please ignore this email.</p>";

        CreateEmailOptions params = CreateEmailOptions.builder()
                .from(fromEmail)
                .to(to)
                .subject("Password Reset Request")
                .html(htmlContent)
                .build();

        try {
            CreateEmailResponse data = resend.emails().send(params);
            logger.info("Password reset email sent. ID: " + data.getId());
        } catch (ResendException e) {
            logger.error("Failed to send password reset email", e);
        }
    }

    @Override
    public void sendBandInvitation(String to, String bandName, String inviterName, String inviteLink) {
        if (resend == null) {
            logger.warn("Skipping email send: Resend client not initialized.");
            return;
        }

        String htmlContent = "<p>You have been invited to join <strong>" + bandName + "</strong> by " + inviterName
                + ".</p>" +
                "<p>Click the link below to check your invitations:</p>" +
                "<a href=\"" + frontendUrl + "/invitations\">View Invitations</a>";

        CreateEmailOptions params = CreateEmailOptions.builder()
                .from(fromEmail)
                .to(to)
                .subject("Invitation to join " + bandName)
                .html(htmlContent)
                .build();

        try {
            CreateEmailResponse data = resend.emails().send(params);
            logger.info("Band invitation email sent. ID: " + data.getId());
        } catch (ResendException e) {
            logger.error("Failed to send band invitation email", e);
        }
    }

    @Override
    public void sendVerificationEmail(String to, String token) {
        if (resend == null) {
            logger.warn("Skipping email send: Resend client not initialized.");
            return;
        }

        String verifyLink = frontendUrl + "/verify-email?token=" + token;

        String htmlContent = "<p>Welcome to Bandanize!</p>" +
                "<p>Please verify your email address to activate your account by clicking the link below:</p>" +
                "<a href=\"" + verifyLink + "\">Verify Email</a>" +
                "<p>This link will expire in 24 hours.</p>";

        CreateEmailOptions params = CreateEmailOptions.builder()
                .from(fromEmail)
                .to(to)
                .subject("Verify your Bandanize account")
                .html(htmlContent)
                .build();

        try {
            CreateEmailResponse data = resend.emails().send(params);
            logger.info("Verification email sent. ID: " + data.getId());
        } catch (ResendException e) {
            logger.error("Failed to send verification email", e);
        }
    }
}
