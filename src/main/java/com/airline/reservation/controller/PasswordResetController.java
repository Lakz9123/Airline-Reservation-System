package com.airline.reservation.controller;

import com.airline.reservation.entity.PasswordResetToken;
import com.airline.reservation.entity.User;
import com.airline.reservation.repository.PasswordResetTokenRepository;
import com.airline.reservation.repository.UserRepository;
import com.airline.reservation.service.EmailService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Controller
public class PasswordResetController {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetController(UserRepository userRepository,
                                   PasswordResetTokenRepository tokenRepository,
                                   EmailService emailService,
                                   PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email,
                                        HttpServletRequest request,
                                        RedirectAttributes redirectAttributes) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "No account found with that email address.");
            return "redirect:/forgot-password";
        }

        User user = userOpt.get();
        String token = UUID.randomUUID().toString();
        
        // Remove any existing token
        tokenRepository.findByUser(user).ifPresent(tokenRepository::delete);
        
        PasswordResetToken resetToken = new PasswordResetToken(token, user, LocalDateTime.now().plusHours(1));
        tokenRepository.save(resetToken);

        String appUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        String resetUrl = appUrl + "/reset-password?token=" + token;
        
        emailService.sendPasswordResetEmail(user.getEmail(), resetUrl);
        
        redirectAttributes.addFlashAttribute("success", "A password reset link has been sent to your email.");
        return "redirect:/forgot-password";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);
        if (tokenOpt.isEmpty() || tokenOpt.get().getExpiryDate().isBefore(LocalDateTime.now())) {
            model.addAttribute("error", "Invalid or expired password reset token.");
            return "reset-password";
        }
        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token,
                                       @RequestParam("password") String password,
                                       RedirectAttributes redirectAttributes) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);
        if (tokenOpt.isEmpty() || tokenOpt.get().getExpiryDate().isBefore(LocalDateTime.now())) {
            redirectAttributes.addFlashAttribute("error", "Invalid or expired password reset token.");
            return "redirect:/reset-password?token=" + token;
        }

        User user = tokenOpt.get().getUser();
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        
        tokenRepository.delete(tokenOpt.get());

        redirectAttributes.addFlashAttribute("success", "Your password has been successfully reset. Please login.");
        return "redirect:/login";
    }
}
