// src/main/java/com/resolvo/backend/ResolvoBackendApplication.java
package com.resolvo.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class ResolvoBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(ResolvoBackendApplication.class, args);
    }
}