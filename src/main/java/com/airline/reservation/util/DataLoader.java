package com.airline.reservation.util;

import com.airline.reservation.entity.Booking;
import com.airline.reservation.entity.Flight;
import com.airline.reservation.entity.User;
import com.airline.reservation.repository.BookingRepository;
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
    private final BookingRepository bookingRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UserRepository userRepository, FlightRepository flightRepository, BookingRepository bookingRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.flightRepository = flightRepository;
        this.bookingRepository = bookingRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // 1. Seed Admin User
        User admin = userRepository.findByEmail("admin@airline.com").orElse(null);
        if (admin == null) {
            admin = new User("System Admin", "admin@airline.com", passwordEncoder.encode("admin123"), "ROLE_ADMIN", true);
            userRepository.save(admin);
        }

        // 2. Seed Sample Users
        User user1 = userRepository.findByEmail("john@example.com").orElse(null);
        if (user1 == null) {
            user1 = new User("John Doe", "john@example.com", passwordEncoder.encode("password123"), "ROLE_USER", true);
            userRepository.save(user1);
        }

        User user2 = userRepository.findByEmail("jane@example.com").orElse(null);
        if (user2 == null) {
            user2 = new User("Jane Smith", "jane@example.com", passwordEncoder.encode("password123"), "ROLE_USER", false);
            userRepository.save(user2);
        }

        // 3. Seed 5 Sample Flights
        if (flightRepository.count() == 0) {
            LocalDateTime now = LocalDateTime.now();

            Flight f1 = new Flight("AA-101", "American Airlines", "New York", "London", 
                    now.plusDays(2).withHour(10).withMinute(0).withSecond(0).withNano(0), 
                    now.plusDays(2).withHour(22).withMinute(30).withSecond(0).withNano(0), 
                    750, 450.00, 150, 148);

            Flight f2 = new Flight("UA-202", "United Airlines", "Chicago", "San Francisco", 
                    now.plusDays(3).withHour(8).withMinute(15).withSecond(0).withNano(0), 
                    now.plusDays(3).withHour(11).withMinute(45).withSecond(0).withNano(0), 
                    210, 250.00, 120, 119);

            Flight f3 = new Flight("DL-303", "Delta Air Lines", "Atlanta", "Miami", 
                    now.plusDays(1).withHour(14).withMinute(30).withSecond(0).withNano(0), 
                    now.plusDays(1).withHour(16).withMinute(45).withSecond(0).withNano(0), 
                    135, 150.00, 100, 100);

            Flight f4 = new Flight("LH-404", "Lufthansa", "Frankfurt", "Tokyo", 
                    now.plusDays(5).withHour(13).withMinute(0).withSecond(0).withNano(0), 
                    now.plusDays(6).withHour(7).withMinute(15).withSecond(0).withNano(0), 
                    675, 850.00, 250, 250);

            Flight f5 = new Flight("EK-505", "Emirates", "Dubai", "Paris", 
                    now.plusDays(4).withHour(6).withMinute(30).withSecond(0).withNano(0), 
                    now.plusDays(4).withHour(11).withMinute(0).withSecond(0).withNano(0), 
                    450, 550.00, 200, 200);

            flightRepository.saveAll(Arrays.asList(f1, f2, f3, f4, f5));

            // 4. Seed Sample Bookings for Admin preview
            Booking b1 = new Booking(user1, f1, "12A, 12B", now.minusDays(1), "CONFIRMED", 900.00);
            Booking b2 = new Booking(user1, f2, "14C", now.minusHours(5), "CONFIRMED", 250.00);
            Booking b3 = new Booking(user2, f1, "15A", now.minusDays(2), "CANCELLED", 450.00);

            bookingRepository.saveAll(Arrays.asList(b1, b2, b3));
        }
    }
}
