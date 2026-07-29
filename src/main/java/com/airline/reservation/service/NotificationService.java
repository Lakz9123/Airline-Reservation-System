package com.airline.reservation.service;

import com.airline.reservation.entity.*;
import com.airline.reservation.repository.BookingRepository;
import com.airline.reservation.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;
    private final BookingRepository bookingRepository;
    private final EmailService emailService;

    public NotificationService(NotificationRepository notificationRepository,
                               BookingRepository bookingRepository,
                               EmailService emailService) {
        this.notificationRepository = notificationRepository;
        this.bookingRepository = bookingRepository;
        this.emailService = emailService;
    }

    /**
     * Notifies all CONFIRMED passengers on a flight of a status change.
     * Guards against duplicate notifications per booking per type.
     */
    @Transactional
    public void notifyFlightStatusChange(Flight flight, NotificationType type, String message) {
        List<Booking> bookings = bookingRepository.findByFlightAndStatus(flight, "CONFIRMED");
        for (Booking booking : bookings) {
            long existing = notificationRepository.countByBookingIdAndType(booking.getId(), type);
            if (existing > 0) {
                logger.info("Skipping duplicate {} notification for booking {}", type, booking.getId());
                continue;
            }
            Notification notification = new Notification(booking, type, message, LocalDateTime.now());
            notificationRepository.save(notification);
            emailService.sendNotificationEmail(notification);
            logger.info("Sent {} notification to user {} for booking {}", type, booking.getUser().getEmail(), booking.getId());
        }
    }

    /**
     * Notifies all CONFIRMED passengers on a flight of a gate/terminal change.
     */
    @Transactional
    public void notifyGateChange(Flight flight) {
        String gate = flight.getGateNumber() != null ? flight.getGateNumber() : "TBA";
        String terminal = flight.getTerminal() != null ? flight.getTerminal() : "TBA";
        String message = "Gate/terminal update for flight " + flight.getFlightNumber()
                + ": Gate " + gate + ", Terminal " + terminal + ". Please check the departure board for the latest information.";
        // Gate changes can happen multiple times, so we allow them by generating a fresh type check
        List<Booking> bookings = bookingRepository.findByFlightAndStatus(flight, "CONFIRMED");
        for (Booking booking : bookings) {
            Notification notification = new Notification(booking, NotificationType.GATE_CHANGED, message, LocalDateTime.now());
            notificationRepository.save(notification);
            emailService.sendNotificationEmail(notification);
            logger.info("Sent GATE_CHANGED notification to user {} for booking {}", booking.getUser().getEmail(), booking.getId());
        }
    }

    /**
     * Returns all notifications for a user, newest first.
     */
    public List<Notification> getNotificationsForUser(User user) {
        return notificationRepository.findByUserOrderBySentAtDesc(user);
    }

    /**
     * Returns unread count for a user.
     */
    public long getUnreadCount(User user) {
        return notificationRepository.countUnreadByUser(user);
    }

    /**
     * Marks a single notification as read.
     */
    @Transactional
    public void markAsRead(Long notificationId, User user) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            if (n.getBooking().getUser().getId().equals(user.getId())) {
                n.setRead(true);
                notificationRepository.save(n);
            }
        });
    }

    /**
     * Marks all notifications for a user as read.
     */
    @Transactional
    public void markAllAsRead(User user) {
        List<Notification> notifications = notificationRepository.findByUserOrderBySentAtDesc(user);
        for (Notification n : notifications) {
            if (!n.isRead()) {
                n.setRead(true);
                notificationRepository.save(n);
            }
        }
    }
}
