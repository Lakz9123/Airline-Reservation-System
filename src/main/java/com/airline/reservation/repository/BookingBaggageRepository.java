package com.airline.reservation.repository;

import com.airline.reservation.entity.Booking;
import com.airline.reservation.entity.BookingBaggage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookingBaggageRepository extends JpaRepository<BookingBaggage, Long> {
    Optional<BookingBaggage> findByBooking(Booking booking);
}
