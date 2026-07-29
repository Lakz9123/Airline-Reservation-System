package com.airline.reservation.service;

import com.airline.reservation.entity.Booking;
import com.airline.reservation.entity.Coupon;
import com.airline.reservation.entity.CouponUsage;
import com.airline.reservation.entity.DiscountType;
import com.airline.reservation.entity.User;
import com.airline.reservation.repository.CouponRepository;
import com.airline.reservation.repository.CouponUsageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponUsageRepository couponUsageRepository;

    public CouponService(CouponRepository couponRepository, CouponUsageRepository couponUsageRepository) {
        this.couponRepository = couponRepository;
        this.couponUsageRepository = couponUsageRepository;
    }

    public List<Coupon> getAllCoupons() {
        return couponRepository.findAll();
    }

    public Optional<Coupon> getCouponById(Long id) {
        return couponRepository.findById(id);
    }

    @Transactional
    public Coupon saveCoupon(Coupon coupon) {
        if (coupon.getCode() != null) {
            coupon.setCode(coupon.getCode().toUpperCase().trim());
        }
        return couponRepository.save(coupon);
    }

    @Transactional
    public void deleteCoupon(Long id) {
        couponRepository.deleteById(id);
    }

    public Optional<Coupon> findByCode(String code) {
        if (code == null || code.isBlank()) return Optional.empty();
        return couponRepository.findByCodeIgnoreCase(code.trim());
    }

    /**
     * Validates a coupon and returns the calculated discount amount.
     * Throws IllegalArgumentException with a clear reason if invalid.
     */
    public BigDecimal validateAndCalculateDiscount(String code, User user, Double bookingAmount) {
        Coupon coupon = findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Invalid coupon code."));

        if (!coupon.isActive()) {
            throw new IllegalArgumentException("This coupon is no longer active.");
        }

        if (coupon.getExpiryDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("This coupon has expired.");
        }

        BigDecimal amount = BigDecimal.valueOf(bookingAmount);

        if (coupon.getMinBookingAmount() != null && amount.compareTo(coupon.getMinBookingAmount()) < 0) {
            throw new IllegalArgumentException("Minimum booking amount of ₹" + coupon.getMinBookingAmount() + " is required.");
        }

        if (coupon.getUsageLimit() != null) {
            long totalUsage = couponUsageRepository.countByCoupon(coupon);
            if (totalUsage >= coupon.getUsageLimit()) {
                throw new IllegalArgumentException("This coupon has reached its maximum usage limit.");
            }
        }

        if (coupon.getUsageLimitPerUser() != null && user != null) {
            long userUsage = couponUsageRepository.countByCouponAndUser(coupon, user);
            if (userUsage >= coupon.getUsageLimitPerUser()) {
                throw new IllegalArgumentException("You have reached the maximum usage limit for this coupon.");
            }
        }

        // Calculate discount
        BigDecimal discount = BigDecimal.ZERO;
        if (coupon.getDiscountType() == DiscountType.FLAT) {
            discount = coupon.getDiscountValue();
        } else if (coupon.getDiscountType() == DiscountType.PERCENTAGE) {
            discount = amount.multiply(coupon.getDiscountValue()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            if (coupon.getMaxDiscountAmount() != null && discount.compareTo(coupon.getMaxDiscountAmount()) > 0) {
                discount = coupon.getMaxDiscountAmount();
            }
        }

        // Ensure discount doesn't exceed total fare
        if (discount.compareTo(amount) > 0) {
            discount = amount;
        }

        return discount;
    }

    @Transactional
    public void recordCouponUsage(Coupon coupon, User user, Booking booking) {
        CouponUsage usage = new CouponUsage(coupon, user, booking, LocalDateTime.now());
        couponUsageRepository.save(usage);
    }
}
