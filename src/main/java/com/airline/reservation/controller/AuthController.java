package com.airline.reservation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

@Controller
public class AuthController {

    @GetMapping("/")
    public String index(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return getDashboardRedirect(authentication);
        }
        return "redirect:/login";
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
