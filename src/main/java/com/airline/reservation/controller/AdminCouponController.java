package com.airline.reservation.controller;

import com.airline.reservation.entity.Coupon;
import com.airline.reservation.service.CouponService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/coupons")
public class AdminCouponController {

    private final CouponService couponService;

    public AdminCouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @GetMapping
    public String listCoupons(Model model) {
        model.addAttribute("coupons", couponService.getAllCoupons());
        return "admin/coupons";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("coupon", new Coupon());
        return "admin/coupon-form";
    }

    @PostMapping("/save")
    public String saveCoupon(@ModelAttribute("coupon") Coupon coupon, RedirectAttributes redirectAttributes) {
        try {
            couponService.saveCoupon(coupon);
            redirectAttributes.addFlashAttribute("success", "Coupon saved successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error saving coupon: " + e.getMessage());
        }
        return "redirect:/admin/coupons";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Coupon coupon = couponService.getCouponById(id).orElse(null);
        if (coupon == null) {
            redirectAttributes.addFlashAttribute("error", "Coupon not found.");
            return "redirect:/admin/coupons";
        }
        model.addAttribute("coupon", coupon);
        return "admin/coupon-form";
    }

    @PostMapping("/delete/{id}")
    public String deleteCoupon(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            couponService.deleteCoupon(id);
            redirectAttributes.addFlashAttribute("success", "Coupon deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Cannot delete coupon, it may be in use.");
        }
        return "redirect:/admin/coupons";
    }

    @PostMapping("/toggle/{id}")
    public String toggleCoupon(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Coupon coupon = couponService.getCouponById(id).orElseThrow(() -> new IllegalArgumentException("Invalid ID"));
            coupon.setActive(!coupon.isActive());
            couponService.saveCoupon(coupon);
            redirectAttributes.addFlashAttribute("success", "Coupon status updated.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating coupon.");
        }
        return "redirect:/admin/coupons";
    }
}
