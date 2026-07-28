package com.airline.reservation.repository;

import com.airline.reservation.entity.Aircraft;
import com.airline.reservation.entity.AircraftStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AircraftRepository extends JpaRepository<Aircraft, Long> {
    Optional<Aircraft> findByAircraftNumberIgnoreCase(String aircraftNumber);
    List<Aircraft> findByStatus(AircraftStatus status);
}
