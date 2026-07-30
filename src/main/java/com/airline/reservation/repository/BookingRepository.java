package com.airline.reservation.repository;

import com.airline.reservation.entity.Booking;
import com.airline.reservation.entity.Flight;
import com.airline.reservation.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserOrderByBookingDateDesc(User user);

    // Get all CONFIRMED seat numbers for a given flight (for seat map)
    @Query("SELECT b.seatNumbers FROM Booking b WHERE b.flight = :flight AND b.status = 'CONFIRMED'")
    List<String> findConfirmedSeatNumbersByFlight(@Param("flight") Flight flight);

    List<Booking> findByStatusAndCheckInStatusAndReminderSentFalseAndFlight_DepartureDateTimeBetween(
            String status, String checkInStatus, java.time.LocalDateTime start, java.time.LocalDateTime end);

    List<Booking> findByFlightAndStatus(Flight flight, String status);

    List<Booking> findByStatusAndDepartureReminderSentFalseAndFlight_DepartureDateTimeBetween(
            String status, java.time.LocalDateTime start, java.time.LocalDateTime end);

    // Analytics queries
    @Query(value = "SELECT FUNCTION('MONTH', b.bookingDate) as month, SUM(b.totalFare) as revenue FROM Booking b WHERE b.status = 'CONFIRMED' AND b.bookingDate >= :startDate GROUP BY FUNCTION('MONTH', b.bookingDate)")
    List<Object[]> getMonthlyRevenueSince(@Param("startDate") java.time.LocalDateTime startDate);

    @Query(value = "SELECT FUNCTION('MONTH', b.bookingDate) as month, COUNT(b) as bookings FROM Booking b WHERE b.status = 'CONFIRMED' AND b.bookingDate >= :startDate GROUP BY FUNCTION('MONTH', b.bookingDate)")
    List<Object[]> getMonthlyBookingsSince(@Param("startDate") java.time.LocalDateTime startDate);

    @Query(value = "SELECT b.flight.originAirport.city, b.flight.destinationAirport.city, COUNT(b) FROM Booking b GROUP BY b.flight.originAirport.city, b.flight.destinationAirport.city ORDER BY COUNT(b) DESC")
    List<Object[]> getTopRoutes();

    @Query(value = "SELECT b.user, SUM(b.totalFare) FROM Booking b WHERE b.status = 'CONFIRMED' GROUP BY b.user ORDER BY SUM(b.totalFare) DESC")
    List<Object[]> getTopCustomersBySpend();

    @Query(value = "SELECT b.flight.airline.airlineName, COUNT(b) FROM Booking b GROUP BY b.flight.airline.airlineName ORDER BY COUNT(b) DESC")
    List<Object[]> getTopAirlinesByBookings();

    long countByStatus(String status);
}
