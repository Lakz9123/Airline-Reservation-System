package com.airline.reservation.repository;

import com.airline.reservation.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {
    @Query("SELECT f FROM Flight f WHERE f.schedule.route.originAirport.id = :originId AND f.schedule.route.destinationAirport.id = :destinationId AND f.departureDateTime >= :startOfDay AND f.departureDateTime <= :endOfDay AND (:airlineName IS NULL OR :airlineName = '' OR LOWER(f.schedule.route.airline.airlineName) LIKE LOWER(CONCAT('%', :airlineName, '%')))")
    List<Flight> searchFlights(
            @Param("originId") Long originId,
            @Param("destinationId") Long destinationId,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay,
            @Param("airlineName") String airlineName
    );

    boolean existsByScheduleRouteAirlineId(Long airlineId);
    
    boolean existsByScheduleAircraft(com.airline.reservation.entity.Aircraft aircraft);
}
