package com.airline.reservation.controller;

import com.airline.reservation.entity.User;
import com.airline.reservation.entity.Wallet;
import com.airline.reservation.service.UserService;
import com.airline.reservation.service.WalletService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/wallets")
public class AdminWalletController {

    private final WalletService walletService;
    private final UserService userService;

    public AdminWalletController(WalletService walletService, UserService userService) {
        this.walletService = walletService;
        this.userService = userService;
    }

    @GetMapping
    public String listWallets(Model model) {
        model.addAttribute("wallets", walletService.getAllWallets());
        return "admin/wallets";
    }

    @GetMapping("/{userId}")
    public String viewUserTransactions(@PathVariable Long userId, Model model) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Wallet wallet = walletService.getOrCreateWallet(user);
        model.addAttribute("wallet", wallet);
        model.addAttribute("transactions", walletService.getTransactions(wallet));
        return "admin/wallet-transactions";
    }
}
