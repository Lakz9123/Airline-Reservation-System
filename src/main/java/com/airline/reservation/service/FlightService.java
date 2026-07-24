package com.airline.reservation.service;

import com.airline.reservation.entity.Flight;
import com.airline.reservation.repository.FlightRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
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
        // Automatically compute duration in minutes if departure & arrival times are set
        if (flight.getDepartureDateTime() != null && flight.getArrivalDateTime() != null) {
            long minutes = Duration.between(flight.getDepartureDateTime(), flight.getArrivalDateTime()).toMinutes();
            flight.setDurationMinutes((int) Math.max(0, minutes));
        }

        if (flight.getId() == null) {
            flight.setAvailableSeats(flight.getTotalSeats());
        } else {
            Flight existing = flightRepository.findById(flight.getId()).orElse(null);
            if (existing != null) {
                int bookedSeats = existing.getTotalSeats() - existing.getAvailableSeats();
                flight.setAvailableSeats(Math.max(0, flight.getTotalSeats() - bookedSeats));
            }
        }
        return flightRepository.save(flight);
    }

    public void deleteFlight(Long id) {
        flightRepository.deleteById(id);
    }

    public long getCount() {
        return flightRepository.count();
    }
}
