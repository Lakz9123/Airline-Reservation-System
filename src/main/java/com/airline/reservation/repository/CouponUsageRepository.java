package com.airline.reservation.repository;

import com.airline.reservation.entity.CouponUsage;
import com.airline.reservation.entity.Coupon;
import com.airline.reservation.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponUsageRepository extends JpaRepository<CouponUsage, Long> {
    long countByCoupon(Coupon coupon);
    long countByCouponAndUser(Coupon coupon, User user);
}
