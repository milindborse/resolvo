package com.resolvo.backend.email;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Sends rich HTML emails using SendGrid API. Runs asynchronously under the core
 * mailTaskExecutor to ensure slow SMTP/API handshakes don't block request threads.
 */
@Slf4j
@Service
public class EmailService {

    @Value("${resolvo.sendgrid.api-key}")
    private String apiKey;

    @Value("${resolvo.sendgrid.from}")
    private String fromEmail;

    @Async("mailTaskExecutor")
    public void sendHtml(String to, String subject, String htmlBody) {
        String activeApiKey = getLiveEnvVar("SENDGRID_API_KEY", apiKey);
        String activeFromEmail = getLiveEnvVar("SENDGRID_FROM", fromEmail);

        if (activeApiKey == null || activeApiKey.isBlank() || activeApiKey.startsWith("your_")
                || activeFromEmail == null || activeFromEmail.isBlank() || activeFromEmail.startsWith("your_")) {
            log.info("\n" +
                    "********************************************************************************\n" +
                    "[EMAIL FALLBACK LOGGER]\n" +
                    "To: {}\n" +
                    "Subject: {}\n" +
                    "Body (HTML):\n" +
                    "{}\n" +
                    "NOTE: SendGrid API key or sender email is not configured in .env. Showing fallback log.\n" +
                    "********************************************************************************",
                    to, subject, htmlBody);
            return;
        }

        Email from = new Email(activeFromEmail);
        Email recipient = new Email(to);
        Content content = new Content("text/html", htmlBody);
        Mail mail = new Mail(from, subject, recipient, content);

        SendGrid sg = new SendGrid(activeApiKey);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                log.info("Email sent successfully to {} via SendGrid API", to);
            } else {
                log.warn("Failed to send email to {} via SendGrid: status={}, body={}", 
                        to, response.getStatusCode(), response.getBody());
            }
        } catch (IOException | RuntimeException ex) {
            log.warn("Failed to send email to {} via SendGrid: {}", to, ex.getMessage());
        }
    }

    private String getLiveEnvVar(String varName, String defaultValue) {
        // 1. Check System environment variables first
        String sysVal = System.getenv(varName);
        if (sysVal != null && !sysVal.isBlank() && !sysVal.startsWith("your_")) {
            return sysVal;
        }

        // 2. Check injected property default value
        if (defaultValue != null && !defaultValue.isBlank() && !defaultValue.startsWith("your_")) {
            return defaultValue;
        }

        // 3. Fall back to manual .env file parsing from disk (trying multiple potential relative directories)
        String[] paths = {".env", "backend/.env", "../.env", "../backend/.env"};
        for (String path : paths) {
            try {
                java.io.File envFile = new java.io.File(path);
                if (envFile.exists()) {
                    java.util.List<String> lines = java.nio.file.Files.readAllLines(envFile.toPath());
                    for (String line : lines) {
                        if (line.trim().startsWith(varName + "=")) {
                            String val = line.substring(line.indexOf("=") + 1).trim();
                            // Strip quotes if any
                            if (val.startsWith("\"") && val.endsWith("\"") && val.length() >= 2) {
                                val = val.substring(1, val.length() - 1);
                            }
                            if (!val.isBlank() && !val.startsWith("your_")) {
                                return val;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                // Ignore and try next path
            }
        }
        return defaultValue;
    }
}
