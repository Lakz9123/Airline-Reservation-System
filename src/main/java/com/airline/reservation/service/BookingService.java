package com.airline.reservation.service;

import com.airline.reservation.entity.Booking;
import com.airline.reservation.entity.Flight;
import com.airline.reservation.entity.User;
import com.airline.reservation.repository.BookingRepository;
import com.airline.reservation.repository.FlightRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final FlightRepository flightRepository;

    public BookingService(BookingRepository bookingRepository, FlightRepository flightRepository) {
        this.bookingRepository = bookingRepository;
        this.flightRepository = flightRepository;
    }

    @Transactional
    public Booking bookFlight(User user, Long flightId, Integer seatsRequested) {
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new IllegalArgumentException("Flight not found with ID: " + flightId));

        if (seatsRequested <= 0) {
            throw new IllegalArgumentException("Seats to book must be at least 1");
        }

        if (flight.getAvailableSeats() < seatsRequested) {
            throw new IllegalArgumentException("Not enough seats available! Requested: " + seatsRequested + ", Available: " + flight.getAvailableSeats());
        }

        // Deduct seats
        flight.setAvailableSeats(flight.getAvailableSeats() - seatsRequested);
        flightRepository.save(flight);

        // Create booking
        Booking booking = new Booking(user, flight, seatsRequested, LocalDateTime.now(), "CONFIRMED");
        return bookingRepository.save(booking);
    }

    @Transactional
    public void cancelBooking(Long bookingId, User user) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + bookingId));

        // Security check: Only the user who booked (or an admin) can cancel
        if (!booking.getUser().getId().equals(user.getId()) && !user.getRole().equals("ROLE_ADMIN")) {
            throw new SecurityException("You do not have permission to cancel this booking!");
        }

        if ("CANCELLED".equals(booking.getStatus())) {
            throw new IllegalArgumentException("Booking is already cancelled.");
        }

        // Restore seats
        Flight flight = booking.getFlight();
        flight.setAvailableSeats(flight.getAvailableSeats() + booking.getNumberOfSeats());
        flightRepository.save(flight);

        // Update status
        booking.setStatus("CANCELLED");
        bookingRepository.save(booking);
    }

    public List<Booking> getBookingsByUser(User user) {
        return bookingRepository.findByUserOrderByBookingDateDesc(user);
    }

    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }
}
