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
    @Query("SELECT f FROM Flight f WHERE f.originAirport.id = :originId AND f.destinationAirport.id = :destinationId AND f.departureDateTime >= :startOfDay AND f.departureDateTime <= :endOfDay AND (:maxFare IS NULL OR f.fare <= :maxFare) AND (:airline IS NULL OR :airline = '' OR LOWER(f.airlineName) LIKE LOWER(CONCAT('%', :airline, '%')))")
    List<Flight> searchFlights(
            @Param("originId") Long originId,
            @Param("destinationId") Long destinationId,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay,
            @Param("maxFare") Double maxFare,
            @Param("airline") String airline
    );
}
