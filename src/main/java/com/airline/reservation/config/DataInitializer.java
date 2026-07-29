package com.airline.reservation.config;

import com.airline.reservation.entity.User;
import com.airline.reservation.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Seeds essential data on startup (idempotent — only creates if not present).
 * Ensures the admin account always exists after a fresh DB, without wiping existing data.
 */
@Configuration
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    public CommandLineRunner seedAdminUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String adminEmail = "admin@skyfly.com";
            if (userRepository.findByEmailIgnoreCase(adminEmail).isEmpty()) {
                User admin = new User();
                admin.setName("SkyFly Admin");
                admin.setEmail(adminEmail);
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole("ROLE_ADMIN");
                admin.setEnabled(true);
                userRepository.save(admin);
                logger.info("✅ Seeded default admin account: {} (password: admin123)", adminEmail);
            } else {
                logger.debug("Admin account already exists, skipping seed.");
            }
        };
    }
}
