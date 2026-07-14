package com.resolvo.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Without this, Spring's @Async falls back to SimpleAsyncTaskExecutor, which
 * spins up a brand-new unbounded thread per call - fine for a demo, risky
 * under real load (a burst of status updates could open dozens of concurrent
 * SMTP connections at once). This bounds it to a small, dedicated pool.
 */
@Configuration
public class AsyncConfig {

    @Bean(name = "mailTaskExecutor")
    public Executor mailTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("resolvo-mail-");
        executor.initialize();
        return executor;
    }
}