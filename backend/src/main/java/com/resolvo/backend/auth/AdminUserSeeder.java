package com.resolvo.backend.auth;

import com.resolvo.backend.common.enums.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminUserSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${resolvo.admin.default-email}")
    private String defaultEmail;

    @Value("${resolvo.admin.default-password}")
    private String defaultPassword;

    @Override
    public void run(String... args) {
        log.info("Checking if default ADMIN account exists...");
        if (!userRepository.existsByRole(UserRole.ADMIN)) {
            log.info("No ADMIN account found in the database. Seeding default ADMIN account...");

            if (defaultPassword == null || defaultPassword.trim().isEmpty()) {
                log.error("=================================================================");
                log.error("CANNOT SEED DEFAULT ADMIN: 'resolvo.admin.default-password' is empty!");
                log.error("Please configure the DEFAULT_ADMIN_PASSWORD environment variable.");
                log.error("=================================================================");
                return;
            }

            User admin = User.builder()
                    .fullName("Default Admin")
                    .email(defaultEmail.toLowerCase().trim())
                    .password(passwordEncoder.encode(defaultPassword))
                    .flatNumber("ADMIN-HQ")
                    .role(UserRole.ADMIN)
                    .enabled(true)
                    .build();

            userRepository.save(admin);
            log.info("Default ADMIN account seeded successfully with email: {}", defaultEmail);
        } else {
            log.info("ADMIN account already exists. Skipping database seeding.");
        }
    }
}
