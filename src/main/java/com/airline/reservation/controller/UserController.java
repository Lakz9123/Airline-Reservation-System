package com.airline.reservation.controller;

import com.airline.reservation.entity.Booking;
import com.airline.reservation.entity.Flight;
import com.airline.reservation.entity.User;
import com.airline.reservation.repository.BookingRepository;
import com.airline.reservation.repository.FlightRepository;
import com.airline.reservation.service.EmailService;
import com.airline.reservation.service.QrCodeService;
import com.airline.reservation.service.TicketPdfService;
import com.airline.reservation.service.UserBookingService;
import com.airline.reservation.service.UserService;
import com.airline.reservation.service.AirportService;
import com.airline.reservation.service.AirlineService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final FlightRepository flightRepository;
    private final UserBookingService userBookingService;
    private final PasswordEncoder passwordEncoder;
    private final TicketPdfService ticketPdfService;
    private final BookingRepository bookingRepository;
    private final EmailService emailService;
    private final QrCodeService qrCodeService;
    private final AirportService airportService;
    private final AirlineService airlineService;

    public UserController(UserService userService,
                          FlightRepository flightRepository,
                          UserBookingService userBookingService,
                          PasswordEncoder passwordEncoder,
                          TicketPdfService ticketPdfService,
                          BookingRepository bookingRepository,
                          EmailService emailService,
                          QrCodeService qrCodeService,
                          AirportService airportService,
                          AirlineService airlineService) {
        this.userService = userService;
        this.flightRepository = flightRepository;
        this.userBookingService = userBookingService;
        this.passwordEncoder = passwordEncoder;
        this.ticketPdfService = ticketPdfService;
        this.bookingRepository = bookingRepository;
        this.emailService = emailService;
        this.qrCodeService = qrCodeService;
        this.airportService = airportService;
        this.airlineService = airlineService;
    }

    // ----- Helper: resolve current User entity -----
    private User getCurrentUser(UserDetails principal) {
        return userService.findByEmail(principal.getUsername())
                .orElseThrow(() -> new IllegalStateException("Logged-in user not found"));
    }

    // ============================
    // 1. Dashboard
    // ============================
    @GetMapping({"/", "/dashboard"})
    public String dashboard(@AuthenticationPrincipal UserDetails principal, Model model) {
        User user = getCurrentUser(principal);
        List<Booking> bookings = userBookingService.getBookingsForUser(user);
        long confirmed = bookings.stream().filter(b -> "CONFIRMED".equals(b.getStatus())).count();
        long cancelled = bookings.stream().filter(b -> "CANCELLED".equals(b.getStatus())).count();
        double totalSpent = bookings.stream()
                .filter(b -> "CONFIRMED".equals(b.getStatus()))
                .mapToDouble(Booking::getTotalFare).sum();
        model.addAttribute("currentUser", user);
        model.addAttribute("confirmedCount", confirmed);
        model.addAttribute("cancelledCount", cancelled);
        model.addAttribute("totalSpent", totalSpent);
        model.addAttribute("recentBookings", bookings.stream().limit(3).toList());
        return "user/dashboard";
    }

    // ============================
    // 2. Flight Search
    // ============================
    @GetMapping("/search")
    public String searchForm(Model model) {
        model.addAttribute("flights", Collections.emptyList());
        model.addAttribute("airports", airportService.getAllAirports());
        model.addAttribute("airlines", airlineService.getAllAirlines());
        return "user/search";
    }

    @GetMapping("/search/results")
    public String searchResults(
            @RequestParam Long originId,
            @RequestParam Long destinationId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Double maxFare,
            @RequestParam(required = false) String airline,
            Model model) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);
        List<Flight> flights = flightRepository.searchFlights(originId, destinationId, startOfDay, endOfDay, maxFare, airline);
        model.addAttribute("flights", flights);
        model.addAttribute("originId", originId);
        model.addAttribute("destinationId", destinationId);
        model.addAttribute("airports", airportService.getAllAirports());
        model.addAttribute("airlines", airlineService.getAllAirlines());
        model.addAttribute("date", date);
        model.addAttribute("maxFare", maxFare);
        model.addAttribute("airline", airline);
        return "user/search";
    }

    // ============================
    // 3. Seat Selection
    // ============================
    @GetMapping("/book/{flightId}")
    public String seatSelection(@PathVariable Long flightId,
                                @AuthenticationPrincipal UserDetails principal,
                                Model model) {
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new IllegalArgumentException("Flight not found"));
        List<String> allSeats = userBookingService.generateSeatLabels(flight);
        Set<String> bookedSeats = userBookingService.getBookedSeats(flight);
        model.addAttribute("flight", flight);
        model.addAttribute("allSeats", allSeats);
        model.addAttribute("bookedSeats", bookedSeats);
        return "user/seat-selection";
    }

    // ============================
    // 4. Payment Mock
    // ============================
    @PostMapping("/book/{flightId}")
    public String proceedToPayment(@PathVariable Long flightId,
                                   @RequestParam(value = "selectedSeats", required = false) List<String> selectedSeats,
                                   @RequestParam(value = "cabinClass", defaultValue = "Economy") String cabinClass,
                                   RedirectAttributes redirectAttributes) {
        if (selectedSeats == null || selectedSeats.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Please select at least one seat.");
            return "redirect:/user/book/" + flightId;
        }
        redirectAttributes.addFlashAttribute("selectedSeats", selectedSeats);
        redirectAttributes.addFlashAttribute("cabinClass", cabinClass);
        return "redirect:/user/payment/" + flightId;
    }

    @GetMapping("/payment/{flightId}")
    public String showPaymentPage(@PathVariable Long flightId,
                                  Model model,
                                  @ModelAttribute("selectedSeats") List<String> selectedSeats,
                                  @ModelAttribute("cabinClass") String cabinClass) {
        if (selectedSeats == null || selectedSeats.isEmpty()) {
            return "redirect:/user/book/" + flightId;
        }
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new IllegalArgumentException("Flight not found"));
        model.addAttribute("flight", flight);
        model.addAttribute("selectedSeats", selectedSeats);
        model.addAttribute("cabinClass", cabinClass == null || cabinClass.isEmpty() ? "Economy" : cabinClass);
        
        double multiplier = 1.0;
        if ("Premium Economy".equalsIgnoreCase(cabinClass)) multiplier = 1.5;
        else if ("Business Class".equalsIgnoreCase(cabinClass)) multiplier = 2.5;
        else if ("First Class".equalsIgnoreCase(cabinClass)) multiplier = 4.0;
        
        model.addAttribute("totalFare", flight.getFare() * multiplier * selectedSeats.size());
        return "user/payment";
    }

    @PostMapping("/payment/{flightId}")
    public String processPayment(@PathVariable Long flightId,
                                 @RequestParam("selectedSeats") List<String> selectedSeats,
                                 @RequestParam("cabinClass") String cabinClass,
                                 @AuthenticationPrincipal UserDetails principal,
                                 RedirectAttributes redirectAttributes) {
        User user = getCurrentUser(principal);
        try {
            // Mock payment processing happens here
            Booking booking = userBookingService.bookSeats(user, flightId, selectedSeats, cabinClass);
            emailService.sendBookingConfirmation(booking);
            redirectAttributes.addFlashAttribute("bookingSuccess", true);
            return "redirect:/user/bookings";
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/user/book/" + flightId;
        }
    }

    // ============================
    // 5. My Bookings
    // ============================
    @GetMapping("/bookings")
    public String myBookings(@AuthenticationPrincipal UserDetails principal, Model model) {
        User user = getCurrentUser(principal);
        List<Booking> bookings = userBookingService.getBookingsForUser(user);
        model.addAttribute("bookings", bookings);
        return "user/bookings";
    }

    @PostMapping("/bookings/cancel/{id}")
    public String cancelBooking(@PathVariable Long id,
                                @AuthenticationPrincipal UserDetails principal,
                                RedirectAttributes redirectAttributes) {
        User user = getCurrentUser(principal);
        try {
            userBookingService.cancelBooking(id, user.getId());
            redirectAttributes.addFlashAttribute("success", "Booking cancelled and seats restored successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/user/bookings";
    }

    // ============================
    // 6. Ticket View (HTML)
    // ============================
    @GetMapping("/ticket/{bookingId}")
    public String viewTicket(@PathVariable Long bookingId,
                             @AuthenticationPrincipal UserDetails principal,
                             Model model) {
        User user = getCurrentUser(principal);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        if (!booking.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Access denied");
        }
        model.addAttribute("booking", booking);

        // Pre-compute display values so Thymeleaf doesn't need complex SpEL
        String ref = String.format("SKY-%05d", booking.getId());
        String originCode = booking.getFlight().getOriginAirport().getAirportCode();
        String destCode = booking.getFlight().getDestinationAirport().getAirportCode();
        String originFull = booking.getFlight().getOriginAirport().getAirportName() + ", " + booking.getFlight().getOriginAirport().getCity();
        String destFull = booking.getFlight().getDestinationAirport().getAirportName() + ", " + booking.getFlight().getDestinationAirport().getCity();
        
        model.addAttribute("bookingRef",  ref);
        model.addAttribute("originCode",  originCode);
        model.addAttribute("destCode",    destCode);
        model.addAttribute("originFull",  originFull);
        model.addAttribute("destFull",    destFull);

        // Generate QR Code
        String qrText = "Booking: " + ref + "\nFlight: " + booking.getFlight().getFlightNumber() + "\nSeats: " + booking.getSeatNumbers();
        String qrBase64 = qrCodeService.generateQrCodeBase64(qrText, 150, 150);
        model.addAttribute("qrBase64", qrBase64);

        return "user/ticket";
    }


    // ============================
    // 7. Ticket PDF Download
    // ============================
    @GetMapping("/ticket/{bookingId}/download")
    public void downloadTicket(@PathVariable Long bookingId,
                               @AuthenticationPrincipal UserDetails principal,
                               HttpServletResponse response) throws Exception {
        User user = getCurrentUser(principal);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        if (!booking.getUser().getId().equals(user.getId())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        String filename = String.format("SkyFly-Ticket-SKY%05d.pdf", booking.getId());
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        ticketPdfService.generate(booking, response.getOutputStream());
        response.flushBuffer();
    }

    // ============================
    // 8. Profile
    // ============================
    @GetMapping("/profile")
    public String viewProfile(@AuthenticationPrincipal UserDetails principal, Model model) {
        User user = getCurrentUser(principal);
        model.addAttribute("currentUser", user);
        return "user/profile";
    }

    @PostMapping("/profile/update-name")
    public String updateName(@AuthenticationPrincipal UserDetails principal,
                             @RequestParam String name,
                             RedirectAttributes redirectAttributes) {
        User user = getCurrentUser(principal);
        if (name == null || name.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("nameError", "Name cannot be empty.");
        } else {
            user.setName(name.trim());
            userService.saveUser(user);
            redirectAttributes.addFlashAttribute("nameSuccess", "Name updated successfully.");
        }
        return "redirect:/user/profile";
    }

    @PostMapping("/profile/change-password")
    public String changePassword(@AuthenticationPrincipal UserDetails principal,
                                 @RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 RedirectAttributes redirectAttributes) {
        User user = getCurrentUser(principal);
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            redirectAttributes.addFlashAttribute("passwordError", "Current password is incorrect.");
        } else if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("passwordError", "New passwords do not match.");
        } else if (newPassword.length() < 6) {
            redirectAttributes.addFlashAttribute("passwordError", "New password must be at least 6 characters.");
        } else {
            user.setPassword(passwordEncoder.encode(newPassword));
            userService.saveUser(user);
            redirectAttributes.addFlashAttribute("passwordSuccess", "Password changed successfully.");
        }
        return "redirect:/user/profile";
    }
}
