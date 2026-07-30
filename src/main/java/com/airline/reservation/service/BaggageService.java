package com.airline.reservation.service;

import com.airline.reservation.entity.BaggageAllowance;
import com.airline.reservation.entity.BaggagePricing;
import com.airline.reservation.repository.BaggageAllowanceRepository;
import com.airline.reservation.repository.BaggagePricingRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BaggageService {

    private final BaggageAllowanceRepository baggageAllowanceRepository;
    private final BaggagePricingRepository baggagePricingRepository;

    public BaggageService(BaggageAllowanceRepository baggageAllowanceRepository, BaggagePricingRepository baggagePricingRepository) {
        this.baggageAllowanceRepository = baggageAllowanceRepository;
        this.baggagePricingRepository = baggagePricingRepository;
    }

    public List<BaggageAllowance> getAllAllowances() {
        return baggageAllowanceRepository.findAll();
    }

    public BaggageAllowance getAllowanceForCabinClass(String cabinClass) {
        if (cabinClass == null) {
            cabinClass = "Economy";
        }
        // Normalize for enum-like string matching if necessary, though DataInitializer will set it as "ECONOMY" etc or "Economy"
        // Let's use the exact string or ignore case
        return baggageAllowanceRepository.findByCabinClassIgnoreCase(cabinClass)
                .orElse(new BaggageAllowance(cabinClass, 7, 15, 1)); // safe fallback
    }

    public void saveAllowance(BaggageAllowance allowance) {
        baggageAllowanceRepository.save(allowance);
    }

    public BaggagePricing getPricing() {
        return baggagePricingRepository.findById(1L)
                .orElse(new BaggagePricing(new BigDecimal("1500.00"), new BigDecimal("500.00")));
    }

    public void savePricing(BaggagePricing pricing) {
        if (pricing.getId() == null) {
            pricing.setId(1L);
        }
        baggagePricingRepository.save(pricing);
    }

    public BigDecimal calculateExtraBaggageCost(int extraBags, int extraKg) {
        BaggagePricing pricing = getPricing();
        BigDecimal bagCost = pricing.getCostPerExtraBag().multiply(new BigDecimal(extraBags));
        BigDecimal kgCost = pricing.getCostPerExtraKg().multiply(new BigDecimal(extraKg));
        return bagCost.add(kgCost);
    }
}
