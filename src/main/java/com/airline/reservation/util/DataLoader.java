package com.airline.reservation.util;

import com.airline.reservation.entity.Booking;
import com.airline.reservation.entity.Flight;
import com.airline.reservation.entity.User;
import com.airline.reservation.entity.Airport;
import com.airline.reservation.entity.Airline;
import com.airline.reservation.entity.Aircraft;
import com.airline.reservation.entity.AircraftStatus;
import com.airline.reservation.repository.BookingRepository;
import com.airline.reservation.repository.FlightRepository;
import com.airline.reservation.repository.UserRepository;
import com.airline.reservation.repository.AirportRepository;
import com.airline.reservation.repository.AirlineRepository;
import com.airline.reservation.repository.AircraftRepository;
import com.airline.reservation.repository.RouteRepository;
import com.airline.reservation.repository.ScheduleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final FlightRepository flightRepository;
    private final BookingRepository bookingRepository;
    private final AirportRepository airportRepository;
    private final AirlineRepository airlineRepository;
    private final AircraftRepository aircraftRepository;
    private final PasswordEncoder passwordEncoder;
    private final RouteRepository routeRepository;
    private final ScheduleRepository scheduleRepository;

    public DataLoader(UserRepository userRepository, FlightRepository flightRepository, BookingRepository bookingRepository, AirportRepository airportRepository, AirlineRepository airlineRepository, AircraftRepository aircraftRepository, PasswordEncoder passwordEncoder, RouteRepository routeRepository, ScheduleRepository scheduleRepository) {
        this.userRepository = userRepository;
        this.flightRepository = flightRepository;
        this.bookingRepository = bookingRepository;
        this.airportRepository = airportRepository;
        this.airlineRepository = airlineRepository;
        this.aircraftRepository = aircraftRepository;
        this.passwordEncoder = passwordEncoder;
        this.routeRepository = routeRepository;
        this.scheduleRepository = scheduleRepository;
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

        // 3. Seed All Indian Airports from CSV
        try {
            Resource resource = new ClassPathResource("airports.csv");
            if (resource.exists()) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
                    String line;
                    boolean header = true;
                    while ((line = br.readLine()) != null) {
                        if (header) { header = false; continue; }
                        String[] data = line.split(",", -1);
                        if (data.length >= 5) {
                            String iata = data[0].trim();
                            String name = data[1].trim();
                            String city = data[2] == null || data[2].trim().isEmpty() ? name : data[2].trim();
                            String country = data[3].trim();
                            String tz = data[4].trim();
                            getOrCreateAirport(iata, name, city, country, 1, tz);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Seed 9 Indian Airlines if none exist
        Airline indigo = null, airIndia = null, spiceJet = null, vistara = null, akasaAir = null;
        if (airlineRepository.count() == 0) {
            indigo = new Airline("IndiGo", "data:image/svg+xml;utf8,%3Csvg%20xmlns%3D%22http%3A//www.w3.org/2000/svg%22%20viewBox%3D%220%200%20400%20150%22%3E%0A%3Crect%20width%3D%22400%22%20height%3D%22150%22%20fill%3D%22%23001B94%22/%3E%0A%3Ctext%20x%3D%2230%22%20y%3D%22100%22%20fill%3D%22white%22%20font-family%3D%22Arial%2C%20sans-serif%22%20font-size%3D%2280%22%20font-weight%3D%22bold%22%3EIndiGo%3C/text%3E%0A%3Ccircle%20cx%3D%22280%22%20cy%3D%2280%22%20r%3D%226%22%20fill%3D%22white%22/%3E%3Ccircle%20cx%3D%22300%22%20cy%3D%2270%22%20r%3D%226%22%20fill%3D%22white%22/%3E%3Ccircle%20cx%3D%22320%22%20cy%3D%2255%22%20r%3D%226%22%20fill%3D%22white%22/%3E%3Ccircle%20cx%3D%22340%22%20cy%3D%2240%22%20r%3D%226%22%20fill%3D%22white%22/%3E%0A%3C/svg%3E", "India", "https://www.goindigo.in", "customer.relations@goindigo.in");
            airIndia = new Airline("Air India", "data:image/svg+xml;utf8,%3Csvg%20xmlns%3D%22http%3A//www.w3.org/2000/svg%22%20viewBox%3D%220%200%20400%20150%22%3E%0A%3Crect%20width%3D%22400%22%20height%3D%22150%22%20fill%3D%22%23E31837%22/%3E%0A%3Ctext%20x%3D%2230%22%20y%3D%22100%22%20fill%3D%22white%22%20font-family%3D%22Arial%2C%20sans-serif%22%20font-size%3D%2260%22%20font-weight%3D%22900%22%3EAIR%20INDIA%3C/text%3E%0A%3Cpath%20d%3D%22M320%2C40%20Q380%2C40%20370%2C110%20Q340%2C60%20320%2C40%22%20fill%3D%22%23F3A71C%22/%3E%0A%3C/svg%3E", "India", "https://www.airindia.com", "contactus@airindia.com");
            spiceJet = new Airline("SpiceJet", "data:image/svg+xml;utf8,%3Csvg%20xmlns%3D%22http%3A//www.w3.org/2000/svg%22%20viewBox%3D%220%200%20400%20150%22%3E%0A%3Crect%20width%3D%22400%22%20height%3D%22150%22%20fill%3D%22%23DA251D%22/%3E%0A%3Ctext%20x%3D%22140%22%20y%3D%22100%22%20fill%3D%22white%22%20font-family%3D%22Arial%2C%20sans-serif%22%20font-size%3D%2270%22%20font-style%3D%22italic%22%20font-weight%3D%22bold%22%3Espicejet%3C/text%3E%0A%3Ccircle%20cx%3D%2250%22%20cy%3D%22110%22%20r%3D%2212%22%20fill%3D%22%23F9A01B%22/%3E%3Ccircle%20cx%3D%2280%22%20cy%3D%2295%22%20r%3D%2216%22%20fill%3D%22%23F9A01B%22/%3E%3Ccircle%20cx%3D%22110%22%20cy%3D%2270%22%20r%3D%2222%22%20fill%3D%22%23F9A01B%22/%3E%3Ccircle%20cx%3D%2280%22%20cy%3D%2245%22%20r%3D%2214%22%20fill%3D%22%23F9A01B%22/%3E%0A%3C/svg%3E", "India", "https://www.spicejet.com", "custrelations@spicejet.com");
            vistara = new Airline("Vistara", "data:image/svg+xml;utf8,%3Csvg%20xmlns%3D%22http%3A//www.w3.org/2000/svg%22%20viewBox%3D%220%200%20400%20150%22%3E%0A%3Crect%20width%3D%22400%22%20height%3D%22150%22%20fill%3D%22%233D1152%22/%3E%0A%3Ctext%20x%3D%22140%22%20y%3D%2295%22%20fill%3D%22white%22%20font-family%3D%22Arial%2C%20sans-serif%22%20font-size%3D%2265%22%20font-weight%3D%22100%22%3Evistara%3C/text%3E%0A%3Cpath%20d%3D%22M70%2C30%20L100%2C75%20L70%2C120%20L40%2C75%20Z%22%20fill%3D%22none%22%20stroke%3D%22%23C0934F%22%20stroke-width%3D%225%22/%3E%0A%3Cpath%20d%3D%22M70%2C50%20L90%2C75%20L70%2C100%20L50%2C75%20Z%22%20fill%3D%22none%22%20stroke%3D%22%23C0934F%22%20stroke-width%3D%223%22/%3E%0A%3C/svg%3E", "India", "https://www.airvistara.com", "custrelations@airvistara.com");
            akasaAir = new Airline("Akasa Air", "data:image/svg+xml;utf8,%3Csvg%20xmlns%3D%22http%3A//www.w3.org/2000/svg%22%20viewBox%3D%220%200%20400%20150%22%3E%0A%3Crect%20width%3D%22400%22%20height%3D%22150%22%20fill%3D%22%234C1C8D%22/%3E%0A%3Ctext%20x%3D%22120%22%20y%3D%22100%22%20fill%3D%22white%22%20font-family%3D%22Arial%2C%20sans-serif%22%20font-size%3D%2265%22%20font-weight%3D%22bold%22%3EAkasa%20Air%3C/text%3E%0A%3Cpath%20d%3D%22M60%2C110%20Q90%2C30%20100%2C20%20Q100%2C60%2085%2C110%20Z%22%20fill%3D%22%23F05E23%22/%3E%0A%3C/svg%3E", "India", "https://www.akasaair.com", "info@akasaair.com");
            Airline aiExpress = new Airline("Air India Express", "data:image/svg+xml;utf8,%3Csvg%20xmlns%3D%22http%3A//www.w3.org/2000/svg%22%20viewBox%3D%220%200%20400%20150%22%3E%0A%3Ctext%20x%3D%2280%22%20y%3D%2250%22%20fill%3D%22%23D91C36%22%20font-family%3D%22Arial%2C%20sans-serif%22%20font-size%3D%2225%22%20font-style%3D%22italic%22%20font-weight%3D%22900%22%3EAIR%20INDIA%3C/text%3E%0A%3Ctext%20x%3D%2220%22%20y%3D%22110%22%20fill%3D%22%23D91C36%22%20font-family%3D%22Arial%2C%20sans-serif%22%20font-size%3D%2275%22%20font-weight%3D%22normal%22%3Eexpress%3C/text%3E%0A%3C/svg%3E", "India", "https://www.airindiaexpress.com", "customersupport@airindiaexpress.com");
            Airline allianceAir = new Airline("Alliance Air", "data:image/svg+xml;utf8,%3Csvg%20xmlns%3D%22http%3A//www.w3.org/2000/svg%22%20viewBox%3D%220%200%20400%20150%22%3E%0A%3Crect%20width%3D%22400%22%20height%3D%22150%22%20fill%3D%22%23001C3A%22/%3E%0A%3Ctext%20x%3D%2230%22%20y%3D%2290%22%20fill%3D%22white%22%20font-family%3D%22Arial%2C%20sans-serif%22%20font-size%3D%2275%22%20font-style%3D%22italic%22%20font-weight%3D%22bold%22%3EAlliance%3C/text%3E%0A%3Cpolygon%20points%3D%22320%2C35%20340%2C35%20325%2C50%22%20fill%3D%22%23F47B20%22/%3E%0A%3Ctext%20x%3D%22160%22%20y%3D%22120%22%20fill%3D%22white%22%20font-family%3D%22Arial%2C%20sans-serif%22%20font-size%3D%2225%22%20font-style%3D%22italic%22%20font-weight%3D%22bold%22%20letter-spacing%3D%225%22%3EAIRLINES%3C/text%3E%0A%3C/svg%3E", "India", "https://www.allianceair.in", "support@allianceair.in");
            Airline starAir = new Airline("Star Air", "data:image/svg+xml;utf8,%3Csvg%20xmlns%3D%22http%3A//www.w3.org/2000/svg%22%20viewBox%3D%220%200%20400%20150%22%3E%0A%3Ctext%20x%3D%2240%22%20y%3D%22100%22%20fill%3D%22%23EE1B24%22%20font-family%3D%22Arial%2C%20sans-serif%22%20font-size%3D%2275%22%20font-style%3D%22italic%22%20font-weight%3D%22900%22%3ESTAR%3C/text%3E%0A%3Ctext%20x%3D%22240%22%20y%3D%22100%22%20fill%3D%22%2300207F%22%20font-family%3D%22Arial%2C%20sans-serif%22%20font-size%3D%2275%22%20font-style%3D%22italic%22%20font-weight%3D%22900%22%3Eair%3C/text%3E%0A%3Cpolygon%20points%3D%22285%2C35%20292%2C50%20308%2C50%20295%2C60%20300%2C75%20285%2C65%20270%2C75%20275%2C60%20262%2C50%20278%2C50%22%20fill%3D%22%2300207F%22/%3E%0A%3C/svg%3E", "India", "https://starair.in", "customercare@starair.in");
            Airline flyBig = new Airline("FlyBig", "data:image/svg+xml;utf8,%3Csvg%20xmlns%3D%22http%3A//www.w3.org/2000/svg%22%20viewBox%3D%220%200%20400%20150%22%3E%0A%3Cpath%20d%3D%22M80%2C80%20Q100%2C120%20120%2C120%20Q160%2C120%20220%2C50%20Q230%2C40%20210%2C35%20Q120%2C70%20120%2C90%20Q110%2C100%20100%2C90%20Z%22%20fill%3D%22%23AD1177%22/%3E%0A%3Ctext%20x%3D%22210%22%20y%3D%22110%22%20fill%3D%22%23AD1177%22%20font-family%3D%22Arial%2C%20sans-serif%22%20font-size%3D%2275%22%20font-weight%3D%22normal%22%3Eflybig.%3C/text%3E%0A%3C/svg%3E", "India", "https://flybig.in", "info@flybig.in");
            
            airlineRepository.saveAll(Arrays.asList(indigo, airIndia, spiceJet, vistara, akasaAir, aiExpress, allianceAir, starAir, flyBig));
        } else {
            indigo = airlineRepository.findByAirlineNameIgnoreCase("IndiGo").orElse(null);
            airIndia = airlineRepository.findByAirlineNameIgnoreCase("Air India").orElse(null);
            spiceJet = airlineRepository.findByAirlineNameIgnoreCase("SpiceJet").orElse(null);
            vistara = airlineRepository.findByAirlineNameIgnoreCase("Vistara").orElse(null);
            akasaAir = airlineRepository.findByAirlineNameIgnoreCase("Akasa Air").orElse(null);
        }

        // Seed 4 Aircraft if none exist
        Aircraft ac1 = null, ac2 = null, ac3 = null, ac4 = null;
        if (aircraftRepository.count() == 0) {
            ac1 = new Aircraft("Boeing 737 Max", "VT-ABC", "Boeing 737", 180, 12, 168, AircraftStatus.ACTIVE);
            ac2 = new Aircraft("Airbus A320neo", "VT-XYZ", "Airbus A320", 150, 10, 140, AircraftStatus.ACTIVE);
            ac3 = new Aircraft("Boeing 777-300", "VT-DEF", "Boeing 777", 250, 40, 210, AircraftStatus.ACTIVE);
            ac4 = new Aircraft("Airbus A321", "VT-PQR", "Airbus A321", 200, 20, 180, AircraftStatus.ACTIVE);
            aircraftRepository.saveAll(Arrays.asList(ac1, ac2, ac3, ac4));
        } else {
            ac1 = aircraftRepository.findByAircraftNumberIgnoreCase("VT-ABC").orElse(null);
            ac2 = aircraftRepository.findByAircraftNumberIgnoreCase("VT-XYZ").orElse(null);
            ac3 = aircraftRepository.findByAircraftNumberIgnoreCase("VT-DEF").orElse(null);
            ac4 = aircraftRepository.findByAircraftNumberIgnoreCase("VT-PQR").orElse(null);
        }

        // 4. Generate Routes, Schedules, and Flights
        List<Airport> allAirports = airportRepository.findAll();
        List<Airline> allAirlines = airlineRepository.findAll();
        List<Aircraft> allAircrafts = aircraftRepository.findAll();

        if (routeRepository.count() == 0 && allAirports.size() > 1 && !allAirlines.isEmpty() && !allAircrafts.isEmpty()) {
            Random rand = new Random();
            List<com.airline.reservation.entity.Route> routesToSave = new ArrayList<>();
            // Generate 30 Routes
            for (int i = 0; i < 30; i++) {
                Airport origin = allAirports.get(rand.nextInt(allAirports.size()));
                Airport dest = allAirports.get(rand.nextInt(allAirports.size()));
                while (origin.getId().equals(dest.getId())) {
                    dest = allAirports.get(rand.nextInt(allAirports.size()));
                }
                Airline airline = allAirlines.get(rand.nextInt(allAirlines.size()));
                int distance = 300 + rand.nextInt(1500); // 300 to 1800 miles
                int durationMinutes = (distance / 5) + 30; // Approx logic
                double baseFare = 2000 + (distance * 2); // Base fare math
                
                com.airline.reservation.entity.Route r = new com.airline.reservation.entity.Route(airline, origin, dest, distance, baseFare, durationMinutes);
                routesToSave.add(r);
            }
            routeRepository.saveAll(routesToSave);

            // Generate Schedules for each Route (e.g. 2 schedules per route)
            List<com.airline.reservation.entity.Schedule> schedulesToSave = new ArrayList<>();
            for (com.airline.reservation.entity.Route route : routesToSave) {
                for (int j = 0; j < 2; j++) {
                    Aircraft aircraft = allAircrafts.get(rand.nextInt(allAircrafts.size()));
                    java.time.DayOfWeek day = java.time.DayOfWeek.values()[rand.nextInt(7)];
                    java.time.LocalTime time = java.time.LocalTime.of(rand.nextInt(24), rand.nextBoolean() ? 0 : 30);
                    com.airline.reservation.entity.Schedule s = new com.airline.reservation.entity.Schedule(route, aircraft, day, time);
                    schedulesToSave.add(s);
                }
            }
            scheduleRepository.saveAll(schedulesToSave);

            // Generate Flights based on Schedules for the next 30 days
            List<Flight> flightsToSave = new ArrayList<>();
            java.time.LocalDate startDate = java.time.LocalDate.now();
            for (int dayOffset = 0; dayOffset < 30; dayOffset++) {
                java.time.LocalDate currentDate = startDate.plusDays(dayOffset);
                for (com.airline.reservation.entity.Schedule schedule : schedulesToSave) {
                    // Just spawn it anyway for data density, ignoring actual DayOfWeek logic for simplicity in demo
                    if (rand.nextDouble() > 0.3) { // 70% chance to fly on this day
                        LocalDateTime dep = LocalDateTime.of(currentDate, schedule.getDepartureTime());
                        LocalDateTime arr = dep.plusMinutes(schedule.getRoute().getStandardDurationMinutes());
                        
                        Flight f = new Flight(schedule, dep, arr, schedule.getAircraft().getCapacity());
                        double fare = schedule.getRoute().getBaseFare() * (0.9 + rand.nextDouble() * 0.4); // Random variance
                        f.setEconomyFare(Math.round(fare * 100.0) / 100.0);
                        f.setPremiumEconomyFare(Math.round(fare * 1.5 * 100.0) / 100.0);
                        f.setBusinessFare(Math.round(fare * 2.5 * 100.0) / 100.0);
                        f.setFirstClassFare(Math.round(fare * 4.0 * 100.0) / 100.0);
                        flightsToSave.add(f);
                    }
                }
            }
            flightRepository.saveAll(flightsToSave);
            
            // Seed a few Bookings for Admin preview
            if (!flightsToSave.isEmpty()) {
                Flight sf = flightsToSave.get(0);
                Booking b1 = new Booking(user1, sf, "12A, 12B", LocalDateTime.now().minusDays(1), "CONFIRMED", sf.getEconomyFare() * 2, "Economy");
                Booking b2 = new Booking(user1, sf, "14C", LocalDateTime.now().minusHours(5), "CONFIRMED", sf.getEconomyFare(), "Economy");
                Booking b3 = new Booking(user2, sf, "15A", LocalDateTime.now().minusDays(2), "CANCELLED", sf.getEconomyFare(), "Economy");
                bookingRepository.saveAll(Arrays.asList(b1, b2, b3));
            }
        }
    }

    private Airport getOrCreateAirport(String code, String name, String city, String country, Integer terminals, String tz) {
        return airportRepository.findByAirportCodeIgnoreCase(code).orElseGet(() -> {
            Airport a = new Airport(code, name, city, country, terminals, tz);
            return airportRepository.save(a);
        });
    }
}
