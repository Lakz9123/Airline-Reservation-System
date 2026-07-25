package com.airline.reservation.service;

import com.airline.reservation.entity.Booking;
import com.airline.reservation.entity.Flight;
import com.airline.reservation.entity.User;
import com.airline.reservation.repository.BookingRepository;
import com.airline.reservation.repository.FlightRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserBookingService {

    private final BookingRepository bookingRepository;
    private final FlightRepository flightRepository;

    public UserBookingService(BookingRepository bookingRepository, FlightRepository flightRepository) {
        this.bookingRepository = bookingRepository;
        this.flightRepository = flightRepository;
    }

    /** Returns all bookings for the given user, newest first. */
    public List<Booking> getBookingsForUser(User user) {
        return bookingRepository.findByUserOrderByBookingDateDesc(user);
    }

    /**
     * Returns a list of all seat labels for a flight (e.g. A1, A2 … Z6),
     * in rows of 6, plus the set of already-booked seat labels.
     */
    public List<String> generateSeatLabels(Flight flight) {
        List<String> seats = new ArrayList<>();
        int total = flight.getTotalSeats();
        int rowNum = 0;
        int count = 0;
        while (count < total) {
            char rowChar = (char) ('A' + rowNum);
            for (int col = 1; col <= 6 && count < total; col++, count++) {
                seats.add("" + rowChar + col);
            }
            rowNum++;
        }
        return seats;
    }

    /**
     * Returns the set of booked seat labels for a flight
     * (only CONFIRMED bookings count).
     */
    public Set<String> getBookedSeats(Flight flight) {
        List<String> rows = bookingRepository.findConfirmedSeatNumbersByFlight(flight);
        Set<String> booked = new HashSet<>();
        for (String row : rows) {
            if (row != null && !row.isBlank()) {
                for (String s : row.split(",")) {
                    booked.add(s.trim());
                }
            }
        }
        return booked;
    }

    /**
     * Books the given seats for a user on a flight.
     * Validates:
     *  - seat count > 0
     *  - no requested seat is already booked (server-side race-condition guard)
     *  - enough availableSeats remain
     * Returns the saved Booking.
     */
    @Transactional
    public Booking bookSeats(User user, Long flightId, List<String> requestedSeats, String cabinClass) {
        if (requestedSeats == null || requestedSeats.isEmpty()) {
            throw new IllegalArgumentException("Please select at least one seat.");
        }

        // Re-fetch inside transaction for optimistic lock
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new IllegalArgumentException("Flight not found."));

        // Server-side seat availability re-validation
        Set<String> alreadyBooked = getBookedSeats(flight);
        List<String> conflicts = requestedSeats.stream()
                .filter(alreadyBooked::contains)
                .collect(Collectors.toList());
        if (!conflicts.isEmpty()) {
            throw new IllegalStateException("Seat(s) " + String.join(", ", conflicts) + " were just booked by someone else. Please re-select.");
        }

        if (flight.getAvailableSeats() < requestedSeats.size()) {
            throw new IllegalStateException("Not enough available seats on this flight.");
        }

        // Decrement available seats
        flight.setAvailableSeats(flight.getAvailableSeats() - requestedSeats.size());
        flightRepository.save(flight);

        // Determine multiplier based on cabin class
        double multiplier = 1.0;
        if ("Premium Economy".equalsIgnoreCase(cabinClass)) multiplier = 1.5;
        else if ("Business Class".equalsIgnoreCase(cabinClass)) multiplier = 2.5;
        else if ("First Class".equalsIgnoreCase(cabinClass)) multiplier = 4.0;
        else cabinClass = "Economy"; // default

        // Compute total fare
        double totalFare = flight.getFare() * multiplier * requestedSeats.size();
        String seatNumbersStr = String.join(", ", requestedSeats);

        Booking booking = new Booking(user, flight, seatNumbersStr, LocalDateTime.now(), "CONFIRMED", totalFare, cabinClass);
        return bookingRepository.save(booking);
    }

    /**
     * Cancels a booking. Enforces ownership: only the booking's owner can cancel.
     * Restores the seats to the flight's availableSeats count.
     */
    @Transactional
    public void cancelBooking(Long bookingId, Long requestingUserId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found."));

        // Security: enforce ownership
        if (!booking.getUser().getId().equals(requestingUserId)) {
            throw new SecurityException("Access denied: you do not own this booking.");
        }

        if (!"CONFIRMED".equals(booking.getStatus())) {
            throw new IllegalStateException("Only CONFIRMED bookings can be cancelled.");
        }

        // Count seats being restored
        long seatCount = Arrays.stream(booking.getSeatNumbers().split(",")).count();

        // Restore seats
        Flight flight = booking.getFlight();
        flight.setAvailableSeats(flight.getAvailableSeats() + (int) seatCount);
        flightRepository.save(flight);

        booking.setStatus("CANCELLED");
        bookingRepository.save(booking);
    }
}
