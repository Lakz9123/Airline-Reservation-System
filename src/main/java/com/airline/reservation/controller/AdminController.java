package com.airline.reservation.controller;

import com.airline.reservation.entity.Flight;
import com.airline.reservation.service.FlightService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final FlightService flightService;

    public AdminController(FlightService flightService) {
        this.flightService = flightService;
    }

    @GetMapping("/flights")
    public String listFlights(Model model) {
        model.addAttribute("flights", flightService.getAllFlights());
        return "admin/flights";
    }

    @GetMapping("/flights/new")
    public String showCreateForm(Model model) {
        model.addAttribute("flight", new Flight());
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
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Flight flight = flightService.getFlightById(id).orElse(null);
        if (flight == null) {
            return "redirect:/admin/flights";
        }
        model.addAttribute("flight", flight);
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
}
