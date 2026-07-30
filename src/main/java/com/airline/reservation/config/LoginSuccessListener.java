package com.airline.reservation.config;

import com.airline.reservation.service.SystemLogService;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class LoginSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {

    private final SystemLogService systemLogService;

    public LoginSuccessListener(SystemLogService systemLogService) {
        this.systemLogService = systemLogService;
    }

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        Object principal = event.getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            systemLogService.logEvent("USER_LOGIN", username, "User logged in successfully");
        }
    }
}
