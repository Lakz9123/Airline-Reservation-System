package com.airline.reservation.repository;

import com.airline.reservation.entity.Notification;
import com.airline.reservation.entity.NotificationType;
import com.airline.reservation.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("SELECT n FROM Notification n WHERE n.booking.user = :user ORDER BY n.sentAt DESC")
    List<Notification> findByUserOrderBySentAtDesc(@Param("user") User user);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.booking.user = :user AND n.isRead = false")
    long countUnreadByUser(@Param("user") User user);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.booking.id = :bookingId AND n.type = :type")
    long countByBookingIdAndType(@Param("bookingId") Long bookingId, @Param("type") NotificationType type);
}
