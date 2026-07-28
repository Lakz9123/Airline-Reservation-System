package com.airline.reservation.repository;

import com.airline.reservation.entity.Airline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AirlineRepository extends JpaRepository<Airline, Long> {
    Optional<Airline> findByAirlineNameIgnoreCase(String airlineName);
}
