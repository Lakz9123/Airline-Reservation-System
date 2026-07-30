package com.airline.reservation.repository;

import com.airline.reservation.entity.BaggageAllowance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BaggageAllowanceRepository extends JpaRepository<BaggageAllowance, Long> {
    Optional<BaggageAllowance> findByCabinClassIgnoreCase(String cabinClass);
}
