package com.airline.reservation.controller;

import com.airline.reservation.entity.Airline;
import com.airline.reservation.service.AirlineService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/airlines")
public class AdminAirlineController {

    private final AirlineService airlineService;

    public AdminAirlineController(AirlineService airlineService) {
        this.airlineService = airlineService;
    }

    @GetMapping
    public String listAirlines(Model model) {
        model.addAttribute("airlines", airlineService.getAllAirlines());
        return "admin/airlines";
    }

    @GetMapping("/new")
    public String showCreateAirlineForm(Model model) {
        model.addAttribute("airline", new Airline());
        return "admin/airline-form";
    }

    @PostMapping
    public String saveAirline(@ModelAttribute("airline") Airline airline, Model model, RedirectAttributes redirectAttributes) {
        try {
            airlineService.saveAirline(airline);
            redirectAttributes.addFlashAttribute("success", "Airline saved successfully.");
            return "redirect:/admin/airlines";
        } catch (Exception e) {
            model.addAttribute("error", "Error saving airline: " + e.getMessage());
            model.addAttribute("airline", airline);
            return "admin/airline-form";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditAirlineForm(@PathVariable("id") Long id, Model model) {
        Airline airline = airlineService.getAirlineById(id).orElse(null);
        if (airline == null) {
            return "redirect:/admin/airlines";
        }
        model.addAttribute("airline", airline);
        return "admin/airline-form";
    }

    @PostMapping("/delete/{id}")
    public String deleteAirline(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            airlineService.deleteAirline(id);
            redirectAttributes.addFlashAttribute("success", "Airline deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting airline: " + e.getMessage());
        }
        return "redirect:/admin/airlines";
    }
}
