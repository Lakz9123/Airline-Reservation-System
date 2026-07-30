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
        if (flight.getOriginAirport() != null && flight.getDestinationAirport() != null) {
            if (flight.getOriginAirport().getId().equals(flight.getDestinationAirport().getId())) {
                throw new IllegalArgumentException("Origin and Destination airports cannot be the same.");
            }
        }

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

    public double getOverallOccupancyPercentage() {
        List<Flight> flights = flightRepository.findAll();
        if (flights.isEmpty()) return 0.0;
        
        long totalCapacity = 0;
        long totalBooked = 0;
        
        for (Flight f : flights) {
            int capacity = f.getAircraft() != null ? (f.getAircraft().getEconomySeats() + f.getAircraft().getBusinessSeats()) : f.getTotalSeats();
            totalCapacity += capacity;
            totalBooked += (capacity - f.getAvailableSeats());
        }
        
        if (totalCapacity == 0) return 0.0;
        return ((double) totalBooked / totalCapacity) * 100;
    }

    public List<java.util.Map<String, Object>> getFlightUtilization() {
        return flightRepository.findAll().stream().map(f -> {
            int capacity = f.getAircraft() != null ? (f.getAircraft().getEconomySeats() + f.getAircraft().getBusinessSeats()) : f.getTotalSeats();
            int booked = capacity - f.getAvailableSeats();
            double utilization = capacity == 0 ? 0 : ((double) booked / capacity) * 100;
            
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("flightNumber", f.getFlightNumber());
            map.put("departure", f.getDepartureDateTime());
            map.put("booked", booked);
            map.put("capacity", capacity);
            map.put("utilization", utilization);
            return map;
        }).sorted((m1, m2) -> Double.compare((Double) m2.get("utilization"), (Double) m1.get("utilization"))).toList();
    }
}
