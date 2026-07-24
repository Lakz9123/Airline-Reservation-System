package com.airline.reservation.service;

import com.airline.reservation.entity.Booking;
import com.airline.reservation.repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Booking saveBooking(Booking booking) {
        return bookingRepository.save(booking);
    }

    public long getBookingCount() {
        return bookingRepository.count();
    }

    public Double getTotalRevenue() {
        return bookingRepository.findAll().stream()
                .filter(b -> "CONFIRMED".equalsIgnoreCase(b.getStatus()))
                .mapToDouble(Booking::getTotalFare)
                .sum();
    }
}
