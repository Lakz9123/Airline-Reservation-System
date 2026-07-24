package com.airline.reservation.controller;

import com.airline.reservation.entity.Booking;
import com.airline.reservation.entity.Flight;
import com.airline.reservation.entity.User;
import com.airline.reservation.service.BookingService;
import com.airline.reservation.service.FlightService;
import com.airline.reservation.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
public class BookingController {

    private final BookingService bookingService;
    private final FlightService flightService;
    private final UserService userService;

    public BookingController(BookingService bookingService, FlightService flightService, UserService userService) {
        this.bookingService = bookingService;
        this.flightService = flightService;
        this.userService = userService;
    }

    @GetMapping("/booking/book")
    public String showBookForm(@RequestParam("flightId") Long flightId, Model model) {
        Flight flight = flightService.getFlightById(flightId).orElse(null);
        if (flight == null) {
            model.addAttribute("error", "Flight not found.");
            return "redirect:/";
        }
        model.addAttribute("flight", flight);
        return "user/book";
    }

    @PostMapping("/booking/book")
    public String bookFlight(
            @RequestParam("flightId") Long flightId,
            @RequestParam("seats") Integer seats,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        try {
            User user = userService.findByUsername(principal.getName())
                    .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));

            bookingService.bookFlight(user, flightId, seats);
            redirectAttributes.addFlashAttribute("success", "Booking confirmed successfully!");
            return "redirect:/bookings";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/booking/book?flightId=" + flightId;
        }
    }

    @GetMapping("/bookings")
    public String listBookings(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));

        List<Booking> bookings = bookingService.getBookingsByUser(user);
        model.addAttribute("bookings", bookings);
        return "user/bookings";
    }

    @PostMapping("/booking/cancel")
    public String cancelBooking(@RequestParam("bookingId") Long bookingId, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByUsername(principal.getName())
                    .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));

            bookingService.cancelBooking(bookingId, user);
            redirectAttributes.addFlashAttribute("success", "Booking cancelled successfully and seats released.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/bookings";
    }
}
