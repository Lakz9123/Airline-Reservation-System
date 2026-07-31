package com.airline.reservation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

@Controller
public class AuthController {
    private final com.airline.reservation.service.AirportService airportService;
    private final com.airline.reservation.service.BookingService bookingService;

    public AuthController(com.airline.reservation.service.AirportService airportService, com.airline.reservation.service.BookingService bookingService) {
        this.airportService = airportService;
        this.bookingService = bookingService;
    }

    @GetMapping("/")
    public String index(Authentication authentication, org.springframework.ui.Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            return getDashboardRedirect(authentication);
        }
        
        model.addAttribute("airports", airportService.getAllAirports());
        model.addAttribute("topRoutes", bookingService.getTopRoutes());
        
        return "landing";
    }

    @GetMapping("/login")
    public String login(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return getDashboardRedirect(authentication);
        }
        return "login";
    }

    private String getDashboardRedirect(Authentication authentication) {
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if ("ROLE_ADMIN".equals(authority.getAuthority())) {
                return "redirect:/admin/dashboard";
            } else if ("ROLE_USER".equals(authority.getAuthority())) {
                return "redirect:/user/dashboard";
            }
        }
        return "redirect:/user/dashboard";
    }
}
