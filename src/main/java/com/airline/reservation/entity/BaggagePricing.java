package com.airline.reservation.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "baggage_pricing")
public class BaggagePricing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal costPerExtraBag;

    @Column(nullable = false)
    private BigDecimal costPerExtraKg;

    public BaggagePricing() {
    }

    public BaggagePricing(BigDecimal costPerExtraBag, BigDecimal costPerExtraKg) {
        this.costPerExtraBag = costPerExtraBag;
        this.costPerExtraKg = costPerExtraKg;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
