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

    public List<Object[]> getMonthlyRevenueSince(java.time.LocalDateTime startDate) {
        return bookingRepository.getMonthlyRevenueSince(startDate);
    }

    public List<Object[]> getMonthlyBookingsSince(java.time.LocalDateTime startDate) {
        return bookingRepository.getMonthlyBookingsSince(startDate);
    }

    public List<Object[]> getTopRoutes() {
        return bookingRepository.getTopRoutes().stream().limit(5).toList();
    }

    public List<Object[]> getTopCustomersBySpend() {
        return bookingRepository.getTopCustomersBySpend().stream().limit(5).toList();
    }

    public List<Object[]> getTopAirlinesByBookings() {
        return bookingRepository.getTopAirlinesByBookings().stream().limit(5).toList();
    }

    public double getCancellationPercentage() {
        long total = bookingRepository.count();
        if (total == 0) return 0.0;
        long cancelled = bookingRepository.countByStatus("CANCELLED");
        return ((double) cancelled / total) * 100;
    }
}
