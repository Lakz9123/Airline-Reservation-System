package com.airline.reservation.controller;

import com.airline.reservation.entity.Airport;
import com.airline.reservation.service.AirportService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/airports")
public class AdminAirportController {

    private final AirportService airportService;

    public AdminAirportController(AirportService airportService) {
        this.airportService = airportService;
    }

    @GetMapping
    public String listAirports(Model model) {
        model.addAttribute("airports", airportService.getAllAirports());
        return "admin/airports";
    }

    @GetMapping("/new")
    public String showCreateAirportForm(Model model) {
        model.addAttribute("airport", new Airport());
        return "admin/airport-form";
    }

    @PostMapping
    public String saveAirport(@ModelAttribute("airport") Airport airport, RedirectAttributes redirectAttributes) {
        try {
            airportService.saveAirport(airport);
            redirectAttributes.addFlashAttribute("success", "Airport saved successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error saving airport: " + e.getMessage());
        }
        return "redirect:/admin/airports";
    }

    @GetMapping("/edit/{id}")
    public String showEditAirportForm(@PathVariable("id") Long id, Model model) {
        Airport airport = airportService.getAirportById(id).orElse(null);
        if (airport == null) {
            return "redirect:/admin/airports";
        }
        model.addAttribute("airport", airport);
        return "admin/airport-form";
    }

    @PostMapping("/delete/{id}")
    public String deleteAirport(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            airportService.deleteAirport(id);
            redirectAttributes.addFlashAttribute("success", "Airport deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting airport: " + e.getMessage());
        }
        return "redirect:/admin/airports";
    }
}
