package com.resolvo.backend.complaint.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.resolvo.backend.common.enums.ComplaintCategory;
import com.resolvo.backend.common.enums.ComplaintPriority;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

/**
 * Calls Groq's chat completion API to suggest a complaint priority based on
 * the description and category. Falls back to LOW on any error — AI suggestion
 * must never block or break complaint creation.
 */
@Slf4j
@Service
public class GroqPriorityService {

    private final RestClient groqRestClient;
    private final ObjectMapper objectMapper;

    @Value("${resolvo.groq.model}")
    private String model;

    @Value("${resolvo.groq.api-key}")
    private String apiKey;

    public GroqPriorityService(@Qualifier("groqRestClient") RestClient groqRestClient, ObjectMapper objectMapper) {
        this.groqRestClient = groqRestClient;
        this.objectMapper = objectMapper;
    }

    private static final String SYSTEM_PROMPT = """
            You are a complaint priority classifier for a residential society maintenance system.
            Given a complaint description and category, classify the priority as exactly one of: HIGH, MEDIUM, or LOW.
            
            Guidelines:
            - HIGH: Safety hazards, water/gas leaks, electrical dangers, security breaches, lift breakdowns, anything that could harm people or cause major property damage
            - MEDIUM: Issues affecting daily life but not immediately dangerous - persistent noise, parking disputes, partial plumbing issues, cleaning backlogs
            - LOW: Minor inconveniences, cosmetic issues, suggestions, non-urgent maintenance requests
            
            Respond with ONLY the priority level (HIGH, MEDIUM, or LOW). No explanation, no punctuation, no other text.
            """;

    public ComplaintPriority suggestPriority(String description, ComplaintCategory category) {
        if (apiKey == null || apiKey.isBlank() || apiKey.startsWith("your_")) {
            log.debug("Groq API key not configured - defaulting to LOW priority");
            return ComplaintPriority.LOW;
        }

        try {
            Map<String, Object> requestBody = Map.of(
                    "model", model,
                    "messages", List.of(
                            Map.of("role", "system", "content", SYSTEM_PROMPT),
                            Map.of("role", "user", "content",
                                    "Category: " + category.name() + "\nDescription: " + description)
                    ),
                    "temperature", 0.1,
                    "max_tokens", 10
            );

            String responseJson = groqRestClient.post()
                    .uri("/chat/completions")
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);

            JsonNode root = objectMapper.readTree(responseJson);
            String content = root.path("choices").path(0).path("message").path("content").asText().trim().toUpperCase();

            ComplaintPriority suggested = ComplaintPriority.valueOf(content);
            log.debug("Groq suggested priority: {} for description: '{}'", suggested, description.substring(0, Math.min(50, description.length())));
            return suggested;

        } catch (Exception ex) {
            log.warn("Groq priority suggestion failed (falling back to LOW): {}", ex.getMessage());
            return ComplaintPriority.LOW;
        }
    }
}
