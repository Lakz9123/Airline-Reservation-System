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
    @Query("SELECT f FROM Flight f WHERE (:originId IS NULL OR f.originAirport.id = :originId) AND (:destinationId IS NULL OR f.destinationAirport.id = :destinationId) AND (cast(:startOfDay as timestamp) IS NULL OR f.departureDateTime >= :startOfDay) AND (cast(:endOfDay as timestamp) IS NULL OR f.departureDateTime <= :endOfDay) AND (:maxFare IS NULL OR f.fare <= :maxFare) AND (:airlineName IS NULL OR :airlineName = '' OR LOWER(f.airline.airlineName) LIKE LOWER(CONCAT('%', :airlineName, '%')))")
    List<Flight> searchFlights(
            @Param("originId") Long originId,
            @Param("destinationId") Long destinationId,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay,
            @Param("maxFare") Double maxFare,
            @Param("airlineName") String airlineName
    );

    boolean existsByAirlineId(Long airlineId);
    
    boolean existsByAircraft(com.airline.reservation.entity.Aircraft aircraft);
    
    List<Flight> findByFlightStatusNot(com.airline.reservation.entity.FlightStatus status);
}
