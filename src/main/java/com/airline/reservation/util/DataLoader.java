package com.airline.reservation.util;

import com.airline.reservation.entity.Flight;
import com.airline.reservation.entity.User;
import com.airline.reservation.repository.FlightRepository;
import com.airline.reservation.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final FlightRepository flightRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UserRepository userRepository, FlightRepository flightRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.flightRepository = flightRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Seed Admin and default User if they do not exist
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ROLE_ADMIN");
            userRepository.save(admin);
        }
        
        if (!userRepository.existsByUsername("user")) {
            User user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setRole("ROLE_USER");
            userRepository.save(user);
        }

        // Seed Flights if the flight table is empty
        if (flightRepository.count() == 0) {
            LocalDateTime now = LocalDateTime.now();

            Flight f1 = new Flight("AA-101", "New York", "London", 
                    now.plusDays(2).withHour(10).withMinute(0).withSecond(0).withNano(0), 
                    now.plusDays(2).withHour(22).withMinute(30).withSecond(0).withNano(0), 
                    150, 150, 450.00);

            Flight f2 = new Flight("UA-202", "Chicago", "San Francisco", 
                    now.plusDays(3).withHour(8).withMinute(15).withSecond(0).withNano(0), 
                    now.plusDays(3).withHour(11).withMinute(45).withSecond(0).withNano(0), 
                    120, 120, 250.00);

            Flight f3 = new Flight("DL-303", "Atlanta", "Miami", 
                    now.plusDays(1).withHour(14).withMinute(30).withSecond(0).withNano(0), 
                    now.plusDays(1).withHour(16).withMinute(45).withSecond(0).withNano(0), 
                    100, 100, 150.00);

            Flight f4 = new Flight("LH-404", "Frankfurt", "Tokyo", 
                    now.plusDays(5).withHour(13).withMinute(0).withSecond(0).withNano(0), 
                    now.plusDays(6).withHour(7).withMinute(15).withSecond(0).withNano(0), 
                    250, 250, 850.00);

            Flight f5 = new Flight("EK-505", "Dubai", "Paris", 
                    now.plusDays(4).withHour(6).withMinute(30).withSecond(0).withNano(0), 
                    now.plusDays(4).withHour(11).withMinute(0).withSecond(0).withNano(0), 
                    200, 200, 550.00);

            Flight f6 = new Flight("SQ-606", "Singapore", "Sydney", 
                    now.plusDays(2).withHour(20).withMinute(15).withSecond(0).withNano(0), 
                    now.plusDays(3).withHour(6).withMinute(30).withSecond(0).withNano(0), 
                    180, 180, 600.00);

            flightRepository.saveAll(Arrays.asList(f1, f2, f3, f4, f5, f6));
        }
    }
}
