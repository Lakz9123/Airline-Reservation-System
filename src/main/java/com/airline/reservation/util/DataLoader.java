package com.airline.reservation.util;

import com.airline.reservation.entity.Booking;
import com.airline.reservation.entity.Flight;
import com.airline.reservation.entity.User;
import com.airline.reservation.entity.Airport;
import com.airline.reservation.entity.Airline;
import com.airline.reservation.repository.BookingRepository;
import com.airline.reservation.repository.FlightRepository;
import com.airline.reservation.repository.UserRepository;
import com.airline.reservation.repository.AirportRepository;
import com.airline.reservation.repository.AirlineRepository;
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
    private final AirportRepository airportRepository;
    private final AirlineRepository airlineRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UserRepository userRepository, FlightRepository flightRepository, BookingRepository bookingRepository, AirportRepository airportRepository, AirlineRepository airlineRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.flightRepository = flightRepository;
        this.bookingRepository = bookingRepository;
        this.airportRepository = airportRepository;
        this.airlineRepository = airlineRepository;
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

        // Seed 6 Indian Airports if none exist
        Airport maa = null, blr = null, del = null, bom = null, hyd = null, ccu = null;
        if (airportRepository.count() == 0) {
            // Note: existing flight data will need reseeding since ddl-auto=update can't migrate strings to FKs.
            maa = new Airport("MAA", "Chennai International Airport", "Chennai", "India", 4, "GMT+5:30");
            blr = new Airport("BLR", "Kempegowda International Airport", "Bengaluru", "India", 2, "GMT+5:30");
            del = new Airport("DEL", "Indira Gandhi International Airport", "New Delhi", "India", 3, "GMT+5:30");
            bom = new Airport("BOM", "Chhatrapati Shivaji Maharaj International Airport", "Mumbai", "India", 2, "GMT+5:30");
            hyd = new Airport("HYD", "Rajiv Gandhi International Airport", "Hyderabad", "India", 1, "GMT+5:30");
            ccu = new Airport("CCU", "Netaji Subhash Chandra Bose International Airport", "Kolkata", "India", 1, "GMT+5:30");
            airportRepository.saveAll(Arrays.asList(maa, blr, del, bom, hyd, ccu));
        } else {
            maa = airportRepository.findByAirportCodeIgnoreCase("MAA").orElse(null);
            blr = airportRepository.findByAirportCodeIgnoreCase("BLR").orElse(null);
            del = airportRepository.findByAirportCodeIgnoreCase("DEL").orElse(null);
            bom = airportRepository.findByAirportCodeIgnoreCase("BOM").orElse(null);
            hyd = airportRepository.findByAirportCodeIgnoreCase("HYD").orElse(null);
            ccu = airportRepository.findByAirportCodeIgnoreCase("CCU").orElse(null);
        }

        // Seed 5 Indian Airlines if none exist
        Airline indigo = null, airIndia = null, spiceJet = null, vistara = null, akasaAir = null;
        if (airlineRepository.count() == 0) {
            indigo = new Airline("IndiGo", "https://upload.wikimedia.org/wikipedia/commons/f/f5/IndiGo_logo.svg", "India", "https://www.goindigo.in", "customer.relations@goindigo.in");
            airIndia = new Airline("Air India", "https://upload.wikimedia.org/wikipedia/commons/d/df/Air_India_Logo_2023.svg", "India", "https://www.airindia.com", "contactus@airindia.com");
            spiceJet = new Airline("SpiceJet", "https://upload.wikimedia.org/wikipedia/commons/e/ee/SpiceJet_logo.svg", "India", "https://www.spicejet.com", "custrelations@spicejet.com");
            vistara = new Airline("Vistara", "https://upload.wikimedia.org/wikipedia/commons/a/ae/Vistara_logo.svg", "India", "https://www.airvistara.com", "custrelations@airvistara.com");
            akasaAir = new Airline("Akasa Air", "https://upload.wikimedia.org/wikipedia/commons/d/da/Akasa_Air_logo.svg", "India", "https://www.akasaair.com", "info@akasaair.com");
            airlineRepository.saveAll(Arrays.asList(indigo, airIndia, spiceJet, vistara, akasaAir));
        } else {
            indigo = airlineRepository.findByAirlineNameIgnoreCase("IndiGo").orElse(null);
            airIndia = airlineRepository.findByAirlineNameIgnoreCase("Air India").orElse(null);
            spiceJet = airlineRepository.findByAirlineNameIgnoreCase("SpiceJet").orElse(null);
            vistara = airlineRepository.findByAirlineNameIgnoreCase("Vistara").orElse(null);
            akasaAir = airlineRepository.findByAirlineNameIgnoreCase("Akasa Air").orElse(null);
        }

        // 3. Seed 5 Sample Flights
        if (flightRepository.count() == 0) {
            LocalDateTime now = LocalDateTime.now();

            Flight f1 = new Flight("6E-101", indigo, del, bom, 
                    now.plusDays(2).withHour(10).withMinute(0).withSecond(0).withNano(0), 
                    now.plusDays(2).withHour(22).withMinute(30).withSecond(0).withNano(0), 
                    750, 450.00, 150, 148);

            Flight f2 = new Flight("AI-202", airIndia, bom, blr, 
                    now.plusDays(3).withHour(8).withMinute(15).withSecond(0).withNano(0), 
                    now.plusDays(3).withHour(11).withMinute(45).withSecond(0).withNano(0), 
                    210, 250.00, 120, 119);

            Flight f3 = new Flight("SG-303", spiceJet, maa, hyd, 
                    now.plusDays(1).withHour(14).withMinute(30).withSecond(0).withNano(0), 
                    now.plusDays(1).withHour(16).withMinute(45).withSecond(0).withNano(0), 
                    135, 150.00, 100, 100);

            Flight f4 = new Flight("UK-404", vistara, del, ccu, 
                    now.plusDays(5).withHour(13).withMinute(0).withSecond(0).withNano(0), 
                    now.plusDays(6).withHour(7).withMinute(15).withSecond(0).withNano(0), 
                    675, 850.00, 250, 250);

            Flight f5 = new Flight("QP-505", akasaAir, blr, maa, 
                    now.plusDays(4).withHour(6).withMinute(30).withSecond(0).withNano(0), 
                    now.plusDays(4).withHour(11).withMinute(0).withSecond(0).withNano(0), 
                    450, 550.00, 200, 200);

            flightRepository.saveAll(Arrays.asList(f1, f2, f3, f4, f5));

            // 4. Seed Sample Bookings for Admin preview
            Booking b1 = new Booking(user1, f1, "12A, 12B", now.minusDays(1), "CONFIRMED", 900.00, "Economy");
            Booking b2 = new Booking(user1, f2, "14C", now.minusHours(5), "CONFIRMED", 250.00, "Economy");
            Booking b3 = new Booking(user2, f1, "15A", now.minusDays(2), "CANCELLED", 450.00, "Economy");

            bookingRepository.saveAll(Arrays.asList(b1, b2, b3));
        }
    }
}
