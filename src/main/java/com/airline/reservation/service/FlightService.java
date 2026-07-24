package com.airline.reservation.service;

import com.airline.reservation.entity.Flight;
import com.airline.reservation.repository.FlightRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FlightService {

    private final FlightRepository flightRepository;

    public FlightService(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    public List<Flight> getAllFlights() {
        return flightRepository.findAll();
    }

    public Optional<Flight> getFlightById(Long id) {
        return flightRepository.findById(id);
    }

    public Flight saveFlight(Flight flight) {
        if (flight.getId() == null) {
            flight.setAvailableSeats(flight.getTotalSeats());
        } else {
            // Keep availableSeats in sync when totalSeats is edited (simple rule)
            Flight existing = flightRepository.findById(flight.getId()).orElse(null);
            if (existing != null) {
                int bookedSeats = existing.getTotalSeats() - existing.getAvailableSeats();
                flight.setAvailableSeats(flight.getTotalSeats() - bookedSeats);
            }
        }
        return flightRepository.save(flight);
    }

    public void deleteFlight(Long id) {
        flightRepository.deleteById(id);
    }

    public List<Flight> searchFlights(String origin, String destination, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);
        return flightRepository.searchFlights(origin, destination, startOfDay, endOfDay);
    }
}
