package com.resolvo.backend.complaint.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Creates a pre-configured RestClient for Groq's OpenAI-compatible API.
 * The API key is injected from resolvo.groq.api-key.
 */
@Configuration
public class GroqConfig {

    @Value("${resolvo.groq.api-key}")
    private String apiKey;

    @Bean("groqRestClient")
    public RestClient groqRestClient() {
        return RestClient.builder()
                .baseUrl("https://api.groq.com/openai/v1")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
