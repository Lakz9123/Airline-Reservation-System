package com.airline.reservation.config;

import com.airline.reservation.service.SystemLogService;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.stereotype.Component;

@Component
public class LoginFailureListener implements ApplicationListener<AbstractAuthenticationFailureEvent> {

    private final SystemLogService systemLogService;

    public LoginFailureListener(SystemLogService systemLogService) {
        this.systemLogService = systemLogService;
    }

    @Override
    public void onApplicationEvent(AbstractAuthenticationFailureEvent event) {
        String username = event.getAuthentication().getName();
        systemLogService.logEvent("LOGIN_FAILED", username, "Failed login attempt: " + event.getException().getMessage());
    }
}
