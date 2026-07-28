package com.airline.reservation.service;

import com.airline.reservation.entity.User;
import com.airline.reservation.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

import org.springframework.security.authentication.DisabledException;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        if (email == null || email.isBlank()) {
            throw new UsernameNotFoundException("Email cannot be empty");
        }
        
        String cleanEmail = email.trim().toLowerCase();
        User user = userRepository.findByEmailIgnoreCase(cleanEmail)
                .orElseThrow(() -> new UsernameNotFoundException("No account found with email: " + cleanEmail));

        if (!user.isEnabled()) {
            throw new DisabledException("Your account has been disabled. Please contact support.");
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.isEnabled(),
                true, // Account Not Expired
                true, // Credentials Not Expired
                true, // Account Not Locked
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole()))
        );
    }
}
