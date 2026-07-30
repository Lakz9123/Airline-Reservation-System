package com.airline.reservation.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "baggage_allowances")
public class BaggageAllowance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String cabinClass; // ECONOMY, PREMIUM_ECONOMY, BUSINESS, FIRST_CLASS

    @Column(nullable = false)
    private Integer cabinBagWeightLimit; // e.g. 7

    @Column(nullable = false)
    private Integer checkedBagWeightLimit; // e.g. 20

    @Column(nullable = false)
    private Integer checkedBagCountIncluded; // e.g. 1

    public BaggageAllowance() {
    }

    public BaggageAllowance(String cabinClass, Integer cabinBagWeightLimit, Integer checkedBagWeightLimit, Integer checkedBagCountIncluded) {
        this.cabinClass = cabinClass;
        this.cabinBagWeightLimit = cabinBagWeightLimit;
        this.checkedBagWeightLimit = checkedBagWeightLimit;
        this.checkedBagCountIncluded = checkedBagCountIncluded;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCabinClass() {
        return cabinClass;
    }

    public void setCabinClass(String cabinClass) {
        this.cabinClass = cabinClass;
    }

    public Integer getCabinBagWeightLimit() {
        return cabinBagWeightLimit;
    }

    public void setCabinBagWeightLimit(Integer cabinBagWeightLimit) {
        this.cabinBagWeightLimit = cabinBagWeightLimit;
    }

    public Integer getCheckedBagWeightLimit() {
        return checkedBagWeightLimit;
    }

    public void setCheckedBagWeightLimit(Integer checkedBagWeightLimit) {
        this.checkedBagWeightLimit = checkedBagWeightLimit;
    }

    public Integer getCheckedBagCountIncluded() {
        return checkedBagCountIncluded;
    }

    public void setCheckedBagCountIncluded(Integer checkedBagCountIncluded) {
        this.checkedBagCountIncluded = checkedBagCountIncluded;
    }
}
