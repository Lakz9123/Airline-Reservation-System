package com.airline.reservation.service;

import com.airline.reservation.entity.Aircraft;
import com.airline.reservation.entity.AircraftStatus;
import com.airline.reservation.repository.AircraftRepository;
import com.airline.reservation.repository.FlightRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AircraftService {

    private final AircraftRepository aircraftRepository;
    private final FlightRepository flightRepository;

    public AircraftService(AircraftRepository aircraftRepository, FlightRepository flightRepository) {
        this.aircraftRepository = aircraftRepository;
        this.flightRepository = flightRepository;
    }

    public List<Aircraft> getAllAircrafts() {
        return aircraftRepository.findAll();
    }

    public List<Aircraft> getActiveAircrafts() {
        return aircraftRepository.findByStatus(AircraftStatus.ACTIVE);
    }

    public Optional<Aircraft> getAircraftById(Long id) {
        return aircraftRepository.findById(id);
    }

    @Transactional
    public Aircraft saveAircraft(Aircraft aircraft) {
        return aircraftRepository.save(aircraft);
    }

    @Transactional
    public void deleteAircraft(Long id) {
        Aircraft aircraft = aircraftRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Aircraft not found"));
                
        if (flightRepository.existsByAircraft(aircraft)) {
            throw new IllegalStateException("Cannot delete aircraft because it is assigned to one or more flights.");
        }
        
        aircraftRepository.delete(aircraft);
    }
}
