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
}
