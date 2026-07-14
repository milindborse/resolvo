// src/main/java/com/resolvo/backend/common/HealthCheckController.java
package com.resolvo.backend.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthCheckController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", Instant.now().toString());
        response.put("service", "resolvo-backend");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/")
    public ResponseEntity<Map<String, String>> root() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Resolvo Backend API");
        response.put("version", "1.0.0");
        response.put("status", "running");
        response.put("docs", "/swagger-ui.html");
        return ResponseEntity.ok(response);
    }
}