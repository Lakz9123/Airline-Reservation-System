package com.airline.reservation.controller;

import com.airline.reservation.entity.User;
import com.airline.reservation.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import java.util.regex.Pattern;

@Controller
public class RegisterController {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private final UserService userService;

    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegisterPage(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                if ("ROLE_ADMIN".equals(authority.getAuthority())) {
                    return "redirect:/admin/dashboard";
                }
            }
            return "redirect:/user/dashboard";
        }
        return "register";
    }

    @PostMapping("/register")
    public String doRegister(@RequestParam String name,
                             @RequestParam String email,
                             @RequestParam String password,
                             @RequestParam String confirmPassword,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        String cleanName = name != null ? name.trim() : "";
        String cleanEmail = email != null ? email.trim().toLowerCase() : "";

        // Retain form inputs in model so they aren't lost on validation error
        model.addAttribute("name", cleanName);
        model.addAttribute("email", cleanEmail);

        if (cleanName.isEmpty()) {
            model.addAttribute("error", "Full name is required.");
            return "register";
        }
        if (cleanEmail.isEmpty() || !EMAIL_PATTERN.matcher(cleanEmail).matches()) {
            model.addAttribute("error", "Please provide a valid email address.");
            return "register";
        }
        if (password == null || password.isEmpty()) {
            model.addAttribute("error", "Password is required.");
            return "register";
        }
        if (password.length() < 6) {
            model.addAttribute("error", "Password must be at least 6 characters long.");
            return "register";
        }
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match.");
            return "register";
        }

        try {
            User newUser = new User(cleanName, cleanEmail, password, "ROLE_USER", true);
            userService.registerUser(newUser);
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please log in.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred during registration. Please try again.");
            return "register";
        }
    }
}
