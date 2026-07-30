package com.airline.reservation.service;

import com.airline.reservation.entity.LoyaltyTier;
import com.airline.reservation.entity.User;
import com.airline.reservation.entity.Wallet;
import com.airline.reservation.repository.WalletRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class LoyaltyService {

    private static final Logger logger = LoggerFactory.getLogger(LoyaltyService.class);

    public static final int MILES_PER_TEN_RUPEES = 1;
    public static final int GOLD_THRESHOLD = 20000;
    public static final int PLATINUM_THRESHOLD = 50000;

    private final WalletService walletService;
    private final WalletRepository walletRepository;

    public LoyaltyService(WalletService walletService, WalletRepository walletRepository) {
        this.walletService = walletService;
        this.walletRepository = walletRepository;
    }

    @Transactional
    public void addMilesForBooking(User user, double finalFare) {
        Wallet wallet = walletService.getOrCreateWallet(user);

        // Calculate base miles
        int baseMiles = (int) Math.floor(finalFare / 10.0) * MILES_PER_TEN_RUPEES;
        if (baseMiles <= 0) return;

        // Apply tier bonus
        double multiplier = 1.0;
        if (wallet.getTier() == LoyaltyTier.GOLD) {
            multiplier = 1.05;
        } else if (wallet.getTier() == LoyaltyTier.PLATINUM) {
            multiplier = 1.10;
        }

        int earnedMiles = (int) Math.floor(baseMiles * multiplier);

        wallet.setTotalMiles(wallet.getTotalMiles() + earnedMiles);
        wallet.setMilesThisYear(wallet.getMilesThisYear() + earnedMiles);

        recalculateTier(wallet);

        walletRepository.save(wallet);
        logger.info("Added {} miles to user {}. Current Tier: {}", earnedMiles, user.getEmail(), wallet.getTier());
    }

    private void recalculateTier(Wallet wallet) {
        int miles = wallet.getMilesThisYear();
        LoyaltyTier newTier = LoyaltyTier.SILVER;

        if (miles >= PLATINUM_THRESHOLD) {
            newTier = LoyaltyTier.PLATINUM;
        } else if (miles >= GOLD_THRESHOLD) {
            newTier = LoyaltyTier.GOLD;
        }

        // Only update if tier increased
        if (newTier.ordinal() > wallet.getTier().ordinal()) {
            wallet.setTier(newTier);
            wallet.setTierValidUntil(LocalDate.now().plusMonths(12));
            logger.info("User {} upgraded to {} tier!", wallet.getUser().getEmail(), newTier);
        } else if (wallet.getTierValidUntil() == null && newTier.ordinal() > 0) {
            wallet.setTierValidUntil(LocalDate.now().plusMonths(12));
        }
    }

    @Transactional
    public void processTierDowngrades() {
        LocalDate today = LocalDate.now();
        List<Wallet> wallets = walletRepository.findAll();
        for (Wallet wallet : wallets) {
            if (wallet.getTierValidUntil() != null && wallet.getTierValidUntil().isBefore(today) || wallet.getTierValidUntil() != null && wallet.getTierValidUntil().isEqual(today)) {
                // Tier expired. Recalculate based on current milesThisYear
                int miles = wallet.getMilesThisYear();
                LoyaltyTier newTier = LoyaltyTier.SILVER;

                if (miles >= PLATINUM_THRESHOLD) {
                    newTier = LoyaltyTier.PLATINUM;
                } else if (miles >= GOLD_THRESHOLD) {
                    newTier = LoyaltyTier.GOLD;
                }

                if (newTier.ordinal() < wallet.getTier().ordinal()) {
                    logger.info("User {} downgraded from {} to {}", wallet.getUser().getEmail(), wallet.getTier(), newTier);
                }

                wallet.setTier(newTier);
                if (newTier != LoyaltyTier.SILVER) {
                    wallet.setTierValidUntil(today.plusMonths(12));
                } else {
                    wallet.setTierValidUntil(null);
                }
                // Reset miles for the new year
                wallet.setMilesThisYear(0);
                walletRepository.save(wallet);
            }
        }
    }
}
