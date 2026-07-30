package com.airline.reservation.repository;

import com.airline.reservation.entity.BaggagePricing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BaggagePricingRepository extends JpaRepository<BaggagePricing, Long> {
}
