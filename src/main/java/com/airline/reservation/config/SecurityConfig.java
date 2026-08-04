package com.airline.reservation.config;

import com.airline.reservation.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserRepository userRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        // Do not hide UsernameNotFoundException, so our failure handler can detect it
        authProvider.setHideUserNotFoundExceptions(false);
        return authProvider;
    }

    @Bean
    public AuthenticationSuccessHandler roleBasedSuccessHandler() {
        return (HttpServletRequest request, HttpServletResponse response, Authentication authentication) -> {
            String redirectUrl = "/login"; // fallback
            for (GrantedAuthority auth : authentication.getAuthorities()) {
                if ("ROLE_ADMIN".equals(auth.getAuthority())) {
                    redirectUrl = "/admin/dashboard";
                    break;
                } else if ("ROLE_USER".equals(auth.getAuthority())) {
                    redirectUrl = "/user/dashboard";
                    break;
                }
            }
            response.sendRedirect(request.getContextPath() + redirectUrl);
        };
    }

    @Bean
    public AuthenticationFailureHandler customFailureHandler() {
        return (HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) -> {
            // Distinguish: email not found vs wrong password
            String errorParam;
            if (exception instanceof UsernameNotFoundException) {
                errorParam = "notfound";
            } else {
                // BadCredentialsException or DisabledException — keep generic message
                errorParam = "badcredentials";
            }
            // Preserve the entered email so the field stays filled
            String email = request.getParameter("email");
            String encodedEmail = email != null
                    ? URLEncoder.encode(email.trim(), StandardCharsets.UTF_8)
                    : "";
            response.sendRedirect(request.getContextPath()
                    + "/login?error=" + errorParam
                    + "&email=" + encodedEmail);
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/register", "/forgot-password", "/reset-password",
                        "/css/**", "/js/**", "/webjars/**", "/h2-console/**", "/images/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .successHandler(roleBasedSuccessHandler())
                .failureHandler(customFailureHandler())   // use custom handler
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .permitAll()
            )
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }
}
