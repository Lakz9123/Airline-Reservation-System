package com.airline.reservation.controller;

import com.airline.reservation.entity.Flight;
import com.airline.reservation.entity.FlightStatus;
import com.airline.reservation.service.BookingService;
import com.airline.reservation.service.FlightService;
import com.airline.reservation.service.UserService;
import com.airline.reservation.service.AirportService;
import com.airline.reservation.service.AirlineService;
import com.airline.reservation.service.AircraftService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final FlightService flightService;
    private final UserService userService;
    private final BookingService bookingService;
    private final AirportService airportService;
    private final AirlineService airlineService;
    private final AircraftService aircraftService;

    public AdminController(FlightService flightService, UserService userService, BookingService bookingService, AirportService airportService, AirlineService airlineService, AircraftService aircraftService) {
        this.flightService = flightService;
        this.userService = userService;
        this.bookingService = bookingService;
        this.airportService = airportService;
        this.airlineService = airlineService;
        this.aircraftService = aircraftService;
    }

    // 1. Dashboard
    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("totalFlights", flightService.getCount());
        model.addAttribute("totalUsers", userService.getAllUsers().size());
        model.addAttribute("totalBookings", bookingService.getBookingCount());
        model.addAttribute("totalRevenue", bookingService.getTotalRevenue());
        return "admin/dashboard";
    }

    // 2. Flight Management (CRUD)
    @GetMapping("/flights")
    public String listFlights(Model model) {
        model.addAttribute("flights", flightService.getAllFlights());
        return "admin/flights";
    }

    @GetMapping("/flights/new")
    public String showCreateFlightForm(Model model) {
        model.addAttribute("flight", new Flight());
        model.addAttribute("airports", airportService.getAllAirports());
        model.addAttribute("airlines", airlineService.getAllAirlines());
        model.addAttribute("aircrafts", aircraftService.getActiveAircrafts());
        return "admin/flight-form";
    }

    @PostMapping("/flights")
    public String saveFlight(@ModelAttribute("flight") Flight flight, RedirectAttributes redirectAttributes) {
        try {
            flightService.saveFlight(flight);
            redirectAttributes.addFlashAttribute("success", "Flight saved successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error saving flight: " + e.getMessage());
        }
        return "redirect:/admin/flights";
    }

    @GetMapping("/flights/edit/{id}")
    public String showEditFlightForm(@PathVariable("id") Long id, Model model) {
        Flight flight = flightService.getFlightById(id).orElse(null);
        if (flight == null) {
            return "redirect:/admin/flights";
        }
        model.addAttribute("flight", flight);
        model.addAttribute("airports", airportService.getAllAirports());
        model.addAttribute("airlines", airlineService.getAllAirlines());
        model.addAttribute("aircrafts", aircraftService.getActiveAircrafts());
        return "admin/flight-form";
    }

    @PostMapping("/flights/delete/{id}")
    public String deleteFlight(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            flightService.deleteFlight(id);
            redirectAttributes.addFlashAttribute("success", "Flight deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting flight: " + e.getMessage());
        }
        return "redirect:/admin/flights";
    }

    @GetMapping("/flights/details/{id}")
    public String showFlightDetails(@PathVariable("id") Long id, Model model) {
        Flight flight = flightService.getFlightById(id).orElse(null);
        if (flight == null) {
            return "redirect:/admin/flights";
        }
        model.addAttribute("flight", flight);
        return "admin/flight-details";
    }

    @PostMapping("/flights/details/{id}/fares")
    public String updateFares(@PathVariable("id") Long id,
                              @RequestParam("economyFare") Double economyFare,
                              @RequestParam("premiumEconomyFare") Double premiumEconomyFare,
                              @RequestParam("businessFare") Double businessFare,
                              @RequestParam("firstClassFare") Double firstClassFare,
                              RedirectAttributes redirectAttributes) {
        Flight flight = flightService.getFlightById(id).orElse(null);
        if (flight != null) {
            flight.setEconomyFare(economyFare);
            flight.setPremiumEconomyFare(premiumEconomyFare);
            flight.setBusinessFare(businessFare);
            flight.setFirstClassFare(firstClassFare);
            flightService.saveFlight(flight);
            redirectAttributes.addFlashAttribute("success", "Fares updated successfully.");
        }
        return "redirect:/admin/flights/details/" + id;
    }

    @PostMapping("/flights/details/{id}/boarding")
    public String updateBoardingInfo(@PathVariable("id") Long id,
                                     @RequestParam(value = "gateNumber", required = false) String gateNumber,
                                     @RequestParam(value = "terminal", required = false) String terminal,
                                     @RequestParam(value = "boardingZone", required = false) String boardingZone,
                                     RedirectAttributes redirectAttributes) {
        Flight flight = flightService.getFlightById(id).orElse(null);
        if (flight != null) {
            flight.setGateNumber(gateNumber);
            flight.setTerminal(terminal);
            flight.setBoardingZone(boardingZone);
            flightService.saveFlight(flight);
            redirectAttributes.addFlashAttribute("success", "Boarding information updated successfully.");
        }
        return "redirect:/admin/flights/details/" + id;
    }

    @PostMapping("/flights/details/{id}/status")
    public String updateStatus(@PathVariable("id") Long id,
                               @RequestParam("flightStatus") FlightStatus flightStatus,
                               RedirectAttributes redirectAttributes) {
        Flight flight = flightService.getFlightById(id).orElse(null);
        if (flight != null) {
            flight.setFlightStatus(flightStatus);
            flightService.saveFlight(flight);
            redirectAttributes.addFlashAttribute("success", "Flight status updated to " + flightStatus.getLabel() + " successfully.");
        }
        return "redirect:/admin/flights/details/" + id;
    }

    // 3. View Bookings (Read-only)
    @GetMapping("/bookings")
    public String listBookings(Model model) {
        model.addAttribute("bookings", bookingService.getAllBookings());
        return "admin/bookings";
    }

    // 4. Manage Users
    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/users";
    }

    @PostMapping("/users/toggle/{id}")
    public String toggleUserStatus(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.toggleUserEnabled(id);
            redirectAttributes.addFlashAttribute("success", "User status updated successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating user: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }
}
