package com.airline.reservation.controller;

import com.airline.reservation.entity.User;
import com.airline.reservation.service.CouponService;
import com.airline.reservation.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/coupons")
public class CouponRestController {

    private final CouponService couponService;
    private final UserService userService;

    public CouponRestController(CouponService couponService, UserService userService) {
        this.couponService = couponService;
        this.userService = userService;
    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateCoupon(
            @RequestParam String code,
            @RequestParam Double originalFare,
            @AuthenticationPrincipal UserDetails principal) {
        
        Map<String, Object> response = new HashMap<>();
        
        if (principal == null) {
            response.put("valid", false);
            response.put("message", "User not authenticated.");
            return ResponseEntity.badRequest().body(response);
        }

        User user = userService.findByEmail(principal.getUsername()).orElse(null);

        try {
            BigDecimal discount = couponService.validateAndCalculateDiscount(code, user, originalFare);
            response.put("valid", true);
            response.put("discountAmount", discount.doubleValue());
            response.put("code", code.toUpperCase().trim());
            response.put("message", "✅ Coupon Applied Successfully — Coupon: " + code.toUpperCase().trim() + " — You Saved ₹" + String.format("%.2f", discount.doubleValue()));
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("valid", false);
            // Convert exception message to exact user request format if possible, otherwise use generic red cross
            String errorMsg = e.getMessage();
            if (errorMsg.contains("expired")) {
                errorMsg = "❌ Coupon Expired";
            } else if (errorMsg.contains("maximum usage limit for this coupon") || errorMsg.contains("has reached its maximum usage limit")) {
                errorMsg = "❌ Coupon Already Used";
            } else if (errorMsg.contains("Invalid coupon code")) {
                errorMsg = "❌ Invalid Coupon Code";
            } else {
                errorMsg = "❌ " + errorMsg;
            }
            response.put("message", errorMsg);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("valid", false);
            response.put("message", "❌ An unexpected error occurred.");
            return ResponseEntity.ok(response);
        }
    }
}
