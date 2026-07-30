package com.airline.reservation.controller;

import com.airline.reservation.entity.BaggageAllowance;
import com.airline.reservation.entity.BaggagePricing;
import com.airline.reservation.service.BaggageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/baggage")
public class AdminBaggageController {

    private final BaggageService baggageService;

    public AdminBaggageController(BaggageService baggageService) {
        this.baggageService = baggageService;
    }

    @GetMapping
    public String viewBaggageSettings(Model model) {
        model.addAttribute("allowances", baggageService.getAllAllowances());
        model.addAttribute("pricing", baggageService.getPricing());
        return "admin/baggage";
    }

    @PostMapping("/allowance/update")
    public String updateAllowance(@ModelAttribute BaggageAllowance allowance, RedirectAttributes redirectAttributes) {
        // Retrieve existing and update fields to preserve ID
        BaggageAllowance existing = baggageService.getAllowanceForCabinClass(allowance.getCabinClass());
        existing.setCabinBagWeightLimit(allowance.getCabinBagWeightLimit());
        existing.setCheckedBagWeightLimit(allowance.getCheckedBagWeightLimit());
        existing.setCheckedBagCountIncluded(allowance.getCheckedBagCountIncluded());
        
        baggageService.saveAllowance(existing);
        redirectAttributes.addFlashAttribute("success", allowance.getCabinClass() + " allowance updated successfully.");
        return "redirect:/admin/baggage";
    }

    @PostMapping("/pricing/update")
    public String updatePricing(@ModelAttribute BaggagePricing pricing, RedirectAttributes redirectAttributes) {
        BaggagePricing existing = baggageService.getPricing();
        existing.setCostPerExtraBag(pricing.getCostPerExtraBag());
        existing.setCostPerExtraKg(pricing.getCostPerExtraKg());
        baggageService.savePricing(existing);
        redirectAttributes.addFlashAttribute("success", "Baggage pricing updated successfully.");
        return "redirect:/admin/baggage";
    }
}
