package com.airline.reservation.service;

import com.airline.reservation.entity.User;
import com.airline.reservation.entity.Wallet;
import com.airline.reservation.repository.UserRepository;
import com.airline.reservation.repository.WalletRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final WalletRepository walletRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, WalletRepository walletRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.walletRepository = walletRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public User registerUser(User user) {
        String normalizedEmail = user.getEmail() != null ? user.getEmail().trim().toLowerCase() : "";
        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new IllegalArgumentException("Email is already registered. Please log in.");
        }
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null || user.getRole().isBlank()) {
            user.setRole("ROLE_USER");
        }
        User savedUser = userRepository.save(user);
        // Auto-create wallet for every new user
        if (walletRepository.findByUserId(savedUser.getId()).isEmpty()) {
            walletRepository.save(new Wallet(savedUser));
        }
        return savedUser;
    }

    public Optional<User> findByEmail(String email) {
        if (email == null) return Optional.empty();
        return userRepository.findByEmailIgnoreCase(email.trim());
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public User toggleUserEnabled(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));
        user.setEnabled(!user.isEnabled());
        return userRepository.save(user);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }
}
