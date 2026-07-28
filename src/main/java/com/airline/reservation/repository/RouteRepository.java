package com.airline.reservation.repository;

import com.airline.reservation.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    List<Route> findByOriginAirportIdAndDestinationAirportId(Long originAirportId, Long destinationAirportId);
}
