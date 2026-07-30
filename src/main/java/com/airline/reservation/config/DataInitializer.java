package com.airline.reservation.config;

import com.airline.reservation.entity.User;
import com.airline.reservation.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.airline.reservation.entity.BaggageAllowance;
import com.airline.reservation.entity.BaggagePricing;
import com.airline.reservation.repository.BaggageAllowanceRepository;
import com.airline.reservation.repository.BaggagePricingRepository;

import com.airline.reservation.entity.Coupon;
import com.airline.reservation.entity.DiscountType;
import com.airline.reservation.repository.CouponRepository;
import java.math.BigDecimal;
import java.time.LocalDate;

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

    @Bean
    public CommandLineRunner seedCoupons(CouponRepository couponRepository) {
        return args -> {
            if (couponRepository.count() == 0) {
                Coupon c1 = new Coupon("AIR10", DiscountType.PERCENTAGE, new BigDecimal("10"), LocalDate.now().plusDays(365));
                
                Coupon c2 = new Coupon("NEWUSER", DiscountType.FLAT, new BigDecimal("500"), LocalDate.now().plusDays(365));
                c2.setUsageLimitPerUser(1);
                
                Coupon c3 = new Coupon("SUMMER50", DiscountType.PERCENTAGE, new BigDecimal("50"), LocalDate.now().plusDays(30));
                c3.setMaxDiscountAmount(new BigDecimal("2000"));
                
                couponRepository.save(c1);
                couponRepository.save(c2);
                couponRepository.save(c3);
                logger.info("✅ Seeded 3 sample coupons.");
            }
        };
    }
    @Bean
    public CommandLineRunner seedBaggageData(BaggageAllowanceRepository allowanceRepo, BaggagePricingRepository pricingRepo) {
        return args -> {
            if (pricingRepo.count() == 0) {
                pricingRepo.save(new BaggagePricing(new java.math.BigDecimal("1500.00"), new java.math.BigDecimal("500.00")));
                logger.info("[OK] Seeded default BaggagePricing.");
            }
            if (allowanceRepo.count() == 0) {
                allowanceRepo.save(new BaggageAllowance("Economy", 7, 15, 1));
                allowanceRepo.save(new BaggageAllowance("Premium Economy", 10, 20, 2));
                allowanceRepo.save(new BaggageAllowance("Business Class", 12, 30, 2));
                allowanceRepo.save(new BaggageAllowance("First Class", 15, 40, 3));
                logger.info("[OK] Seeded default BaggageAllowances.");
            }
        };
    }
}
