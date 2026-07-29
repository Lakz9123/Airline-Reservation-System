package com.airline.reservation.controller;

import com.airline.reservation.entity.Notification;
import com.airline.reservation.entity.User;
import com.airline.reservation.service.NotificationService;
import com.airline.reservation.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/user/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    public NotificationController(NotificationService notificationService, UserService userService) {
        this.notificationService = notificationService;
        this.userService = userService;
    }

    @GetMapping
    public String listNotifications(@AuthenticationPrincipal UserDetails principal, Model model) {
        User user = userService.findByEmail(principal.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        List<Notification> notifications = notificationService.getNotificationsForUser(user);
        long unreadCount = notificationService.getUnreadCount(user);
        model.addAttribute("notifications", notifications);
        model.addAttribute("unreadCount", unreadCount);
        return "user/notifications";
    }

    @PostMapping("/read/{id}")
    public String markAsRead(@PathVariable Long id,
                             @AuthenticationPrincipal UserDetails principal,
                             RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(principal.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        notificationService.markAsRead(id, user);
        return "redirect:/user/notifications";
    }

    @PostMapping("/read-all")
    public String markAllAsRead(@AuthenticationPrincipal UserDetails principal) {
        User user = userService.findByEmail(principal.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        notificationService.markAllAsRead(user);
        return "redirect:/user/notifications";
    }
}
