package com.airline.reservation.service;

import com.airline.reservation.entity.Airline;
import com.airline.reservation.repository.AirlineRepository;
import com.airline.reservation.repository.FlightRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@Transactional
public class AirlineService {

    private final AirlineRepository airlineRepository;
    private final FlightRepository flightRepository;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public AirlineService(AirlineRepository airlineRepository, FlightRepository flightRepository) {
        this.airlineRepository = airlineRepository;
        this.flightRepository = flightRepository;
    }

    public List<Airline> getAllAirlines() {
        return airlineRepository.findAll();
    }

    public Optional<Airline> getAirlineById(Long id) {
        return airlineRepository.findById(id);
    }

    public Airline saveAirline(Airline airline) {
        // Validate supportEmail format at service level
        if (airline.getSupportEmail() == null || !EMAIL_PATTERN.matcher(airline.getSupportEmail()).matches()) {
            throw new IllegalArgumentException("Invalid support email format.");
        }

        // Validate airlineName uniqueness at service level
        Optional<Airline> existing = airlineRepository.findByAirlineNameIgnoreCase(airline.getAirlineName());
        if (existing.isPresent() && !existing.get().getId().equals(airline.getId())) {
            throw new IllegalArgumentException("Airline name must be unique.");
        }

        return airlineRepository.save(airline);
    }

    public void deleteAirline(Long id) {
        if (flightRepository.existsByScheduleRouteAirlineId(id)) {
            throw new IllegalStateException("Cannot delete airline because it is still referenced by existing flights.");
        }
        airlineRepository.deleteById(id);
    }
}
