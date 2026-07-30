package com.airline.reservation.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "booking_baggage")
public class BookingBaggage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Column(nullable = false)
    private Integer extraCheckedBagCount = 0;

    @Column(nullable = false)
    private Integer extraWeightKg = 0;

    @Column(nullable = false)
    private BigDecimal costPerExtraBag = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal costPerExtraKg = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal totalBaggageCost = BigDecimal.ZERO;

    public BookingBaggage() {
    }

    public BookingBaggage(Booking booking, Integer extraCheckedBagCount, Integer extraWeightKg, BigDecimal costPerExtraBag, BigDecimal costPerExtraKg, BigDecimal totalBaggageCost) {
        this.booking = booking;
        this.extraCheckedBagCount = extraCheckedBagCount;
        this.extraWeightKg = extraWeightKg;
        this.costPerExtraBag = costPerExtraBag;
        this.costPerExtraKg = costPerExtraKg;
        this.totalBaggageCost = totalBaggageCost;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public Integer getExtraCheckedBagCount() {
        return extraCheckedBagCount;
    }

    public void setExtraCheckedBagCount(Integer extraCheckedBagCount) {
        this.extraCheckedBagCount = extraCheckedBagCount;
    }

    public Integer getExtraWeightKg() {
        return extraWeightKg;
    }

    public void setExtraWeightKg(Integer extraWeightKg) {
        this.extraWeightKg = extraWeightKg;
    }

    public BigDecimal getCostPerExtraBag() {
        return costPerExtraBag;
    }

    public void setCostPerExtraBag(BigDecimal costPerExtraBag) {
        this.costPerExtraBag = costPerExtraBag;
    }

    public BigDecimal getCostPerExtraKg() {
        return costPerExtraKg;
    }

    public void setCostPerExtraKg(BigDecimal costPerExtraKg) {
        this.costPerExtraKg = costPerExtraKg;
    }

    public BigDecimal getTotalBaggageCost() {
        return totalBaggageCost;
    }

    public void setTotalBaggageCost(BigDecimal totalBaggageCost) {
        this.totalBaggageCost = totalBaggageCost;
    }
}
