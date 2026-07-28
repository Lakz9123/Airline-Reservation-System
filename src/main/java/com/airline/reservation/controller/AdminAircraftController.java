package com.airline.reservation.controller;

import com.airline.reservation.entity.Aircraft;
import com.airline.reservation.entity.AircraftStatus;
import com.airline.reservation.service.AircraftService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/aircraft")
public class AdminAircraftController {

    private final AircraftService aircraftService;

    public AdminAircraftController(AircraftService aircraftService) {
        this.aircraftService = aircraftService;
    }

    @GetMapping
    public String listAircrafts(Model model) {
        model.addAttribute("aircrafts", aircraftService.getAllAircrafts());
        return "admin/aircrafts";
    }

    @GetMapping("/new")
    public String showCreateAircraftForm(Model model) {
        model.addAttribute("aircraft", new Aircraft());
        model.addAttribute("statuses", AircraftStatus.values());
        return "admin/aircraft-form";
    }

    @PostMapping
    public String saveAircraft(@ModelAttribute("aircraft") Aircraft aircraft, Model model, RedirectAttributes redirectAttributes) {
        try {
            aircraftService.saveAircraft(aircraft);
            redirectAttributes.addFlashAttribute("success", "Aircraft saved successfully.");
            return "redirect:/admin/aircraft";
        } catch (Exception e) {
            model.addAttribute("error", "Error saving aircraft: " + e.getMessage());
            model.addAttribute("aircraft", aircraft);
            model.addAttribute("statuses", AircraftStatus.values());
            return "admin/aircraft-form";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditAircraftForm(@PathVariable("id") Long id, Model model) {
        Aircraft aircraft = aircraftService.getAircraftById(id).orElse(null);
        if (aircraft == null) {
            return "redirect:/admin/aircraft";
        }
        model.addAttribute("aircraft", aircraft);
        model.addAttribute("statuses", AircraftStatus.values());
        return "admin/aircraft-form";
    }

    @PostMapping("/delete/{id}")
    public String deleteAircraft(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            aircraftService.deleteAircraft(id);
            redirectAttributes.addFlashAttribute("success", "Aircraft deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting aircraft: " + e.getMessage());
        }
        return "redirect:/admin/aircraft";
    }
}
