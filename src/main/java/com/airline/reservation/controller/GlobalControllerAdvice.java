package com.airline.reservation.controller;

import com.airline.reservation.entity.User;
import com.airline.reservation.service.NotificationService;
import com.airline.reservation.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final NotificationService notificationService;
    private final UserService userService;

    public GlobalControllerAdvice(NotificationService notificationService, UserService userService) {
        this.notificationService = notificationService;
        this.userService = userService;
    }

    @ModelAttribute("unreadNotificationCount")
    public long unreadNotificationCount(@AuthenticationPrincipal UserDetails principal) {
        if (principal == null) return 0L;
        try {
            User user = userService.findByEmail(principal.getUsername()).orElse(null);
            if (user == null) return 0L;
            return notificationService.getUnreadCount(user);
        } catch (Exception e) {
            return 0L;
        }
    }
}
