package com.airline.reservation.service;

import com.airline.reservation.entity.*;
import com.airline.reservation.repository.WalletRepository;
import com.airline.reservation.repository.WalletTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class WalletService {

    // 1 reward point = ₹1
    public static final BigDecimal POINTS_TO_RUPEE_RATE = BigDecimal.ONE;
    // 2% of fare = reward points awarded
    public static final double REWARD_PERCENT = 0.02;

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;

    public WalletService(WalletRepository walletRepository,
                         WalletTransactionRepository walletTransactionRepository) {
        this.walletRepository = walletRepository;
        this.walletTransactionRepository = walletTransactionRepository;
    }

    /** Gets or creates a wallet for the user */
    @Transactional
    public Wallet getOrCreateWallet(User user) {
        return walletRepository.findByUser(user)
                .orElseGet(() -> walletRepository.save(new Wallet(user)));
    }

    public Optional<Wallet> findByUser(User user) {
        return walletRepository.findByUser(user);
    }

    public List<WalletTransaction> getTransactions(Wallet wallet) {
        return walletTransactionRepository.findByWalletOrderByCreatedAtDesc(wallet);
    }

    public List<Wallet> getAllWallets() {
        return walletRepository.findAll();
    }

    /** Credits wallet balance and logs the transaction */
    @Transactional
    public void creditWallet(User user, BigDecimal amount, TransactionCategory category,
                              String description, Booking relatedBooking) {
        Wallet wallet = getOrCreateWallet(user);
        wallet.setBalance(wallet.getBalance().add(amount));
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);

        WalletTransaction tx = new WalletTransaction(wallet, TransactionType.CREDIT, category,
                amount, description, relatedBooking);
        walletTransactionRepository.save(tx);
    }

    /** Debits wallet balance with sufficient-funds check */
    @Transactional
    public void debitWallet(User user, BigDecimal amount, TransactionCategory category,
                             String description, Booking relatedBooking) {
        Wallet wallet = getOrCreateWallet(user);
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient wallet balance.");
        }
        wallet.setBalance(wallet.getBalance().subtract(amount));
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);

        WalletTransaction tx = new WalletTransaction(wallet, TransactionType.DEBIT, category,
                amount, description, relatedBooking);
        walletTransactionRepository.save(tx);
    }

    /** Awards reward points (stored as integer, logged as credit) */
    @Transactional
    public void addRewardPoints(User user, int points, String description, Booking relatedBooking) {
        if (points <= 0) return;
        Wallet wallet = getOrCreateWallet(user);
        wallet.setRewardPoints(wallet.getRewardPoints() + points);
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);

        // Log as a credit transaction of type REWARD_CREDIT (amount = points * rate, informational)
        BigDecimal creditValue = BigDecimal.valueOf(points).multiply(POINTS_TO_RUPEE_RATE);
        WalletTransaction tx = new WalletTransaction(wallet, TransactionType.CREDIT,
                TransactionCategory.REWARD_CREDIT, creditValue, description, relatedBooking);
        walletTransactionRepository.save(tx);
    }

    /** Redeems reward points → converts to wallet cash */
    @Transactional
    public BigDecimal redeemRewardPoints(User user, int points) {
        Wallet wallet = getOrCreateWallet(user);
        if (wallet.getRewardPoints() < points) {
            throw new IllegalStateException("Not enough reward points.");
        }
        BigDecimal creditValue = BigDecimal.valueOf(points).multiply(POINTS_TO_RUPEE_RATE);
        wallet.setRewardPoints(wallet.getRewardPoints() - points);
        wallet.setBalance(wallet.getBalance().add(creditValue));
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);

        WalletTransaction tx = new WalletTransaction(wallet, TransactionType.CREDIT,
                TransactionCategory.REWARD_REDEMPTION, creditValue,
                "Redeemed " + points + " reward points for ₹" + creditValue, null);
        walletTransactionRepository.save(tx);
        return creditValue;
    }

    /** Calculates reward points to award for a booking (2% of fare, rounded down) */
    public int calculateRewardPoints(double fare) {
        return (int) Math.floor(fare * REWARD_PERCENT);
    }
}
