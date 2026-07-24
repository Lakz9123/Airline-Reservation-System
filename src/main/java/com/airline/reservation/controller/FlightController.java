package com.airline.reservation.controller;

import com.airline.reservation.entity.Flight;
import com.airline.reservation.service.FlightService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @GetMapping("/")
    public String index(Model model) {
        return "index";
    }

    @GetMapping("/search")
    public String searchFlights(
            @RequestParam(value = "origin", required = false) String origin,
            @RequestParam(value = "destination", required = false) String destination,
            @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model) {

        if (origin == null || destination == null || date == null || origin.trim().isEmpty() || destination.trim().isEmpty()) {
            model.addAttribute("error", "Please fill in all search fields (Origin, Destination, and Departure Date).");
            return "index";
        }

        try {
            List<Flight> flights = flightService.searchFlights(origin.trim(), destination.trim(), date);
            model.addAttribute("flights", flights);
            model.addAttribute("origin", origin);
            model.addAttribute("destination", destination);
            model.addAttribute("date", date);
            model.addAttribute("searched", true);
        } catch (Exception e) {
            model.addAttribute("error", "Error occurred while searching: " + e.getMessage());
        }
        return "index";
    }
}
