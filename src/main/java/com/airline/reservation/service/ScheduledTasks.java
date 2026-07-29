package com.airline.reservation.service;

import com.airline.reservation.entity.Booking;
import com.airline.reservation.entity.Notification;
import com.airline.reservation.entity.NotificationType;
import com.airline.reservation.repository.BookingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ScheduledTasks {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

    private final BookingRepository bookingRepository;
    private final EmailService emailService;
    private final NotificationService notificationService;
    private final com.airline.reservation.repository.NotificationRepository notificationRepository;

    public ScheduledTasks(BookingRepository bookingRepository,
                          EmailService emailService,
                          NotificationService notificationService,
                          com.airline.reservation.repository.NotificationRepository notificationRepository) {
        this.bookingRepository = bookingRepository;
        this.emailService = emailService;
        this.notificationService = notificationService;
        this.notificationRepository = notificationRepository;
    }

    /**
     * Runs every hour to find bookings departing in 24–48 hours
     * that haven't been checked in or reminded yet.
     */
    @Scheduled(fixedRate = 3600000, initialDelay = 10000)
    @Transactional
    public void sendCheckInReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime windowStart = now.plusHours(24);
        LocalDateTime windowEnd = now.plusHours(48);

        logger.info("Running scheduled check-in reminder job for departures between {} and {}", windowStart, windowEnd);

        List<Booking> eligibleBookings = bookingRepository
                .findByStatusAndCheckInStatusAndReminderSentFalseAndFlight_DepartureDateTimeBetween(
                        "CONFIRMED", "NOT_CHECKED_IN", windowStart, windowEnd);

        if (eligibleBookings.isEmpty()) {
            logger.info("No eligible bookings found for check-in reminder.");
            return;
        }

        for (Booking booking : eligibleBookings) {
            try {
                emailService.sendCheckInReminder(booking);
                booking.setReminderSent(true);
                bookingRepository.save(booking);
                logger.info("Sent check-in reminder and updated flag for booking ID {}", booking.getId());
            } catch (Exception e) {
                logger.error("Failed to process check-in reminder for booking ID {}", booking.getId(), e);
            }
        }
    }

    /**
     * Runs every hour to find bookings departing in ~3 hours that haven't
     * received a departure reminder notification yet.
     */
    @Scheduled(fixedRate = 3600000, initialDelay = 30000)
    @Transactional
    public void sendDepartureReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime windowStart = now.plusHours(2);
        LocalDateTime windowEnd = now.plusHours(4);

        logger.info("Running departure reminder job for departures between {} and {}", windowStart, windowEnd);

        List<Booking> eligibleBookings = bookingRepository
                .findByStatusAndDepartureReminderSentFalseAndFlight_DepartureDateTimeBetween(
                        "CONFIRMED", windowStart, windowEnd);

        if (eligibleBookings.isEmpty()) {
            logger.info("No eligible bookings found for departure reminder.");
            return;
        }

        for (Booking booking : eligibleBookings) {
            try {
                String flightNumber = booking.getFlight().getFlightNumber();
                String origin = booking.getFlight().getOriginAirport().getAirportCode();
                String destination = booking.getFlight().getDestinationAirport().getAirportCode();
                String departureTime = booking.getFlight().getDepartureDateTime()
                        .format(java.time.format.DateTimeFormatter.ofPattern("HH:mm, dd MMM yyyy"));

                String message = "Your flight " + flightNumber + " from " + origin + " to " + destination
                        + " departs at " + departureTime + ". Please proceed to the gate on time.";

                Notification notification = new Notification(booking, NotificationType.DEPARTURE_REMINDER, message, LocalDateTime.now());
                notificationRepository.save(notification);
                emailService.sendNotificationEmail(notification);

                booking.setDepartureReminderSent(true);
                bookingRepository.save(booking);
                logger.info("Sent departure reminder for booking ID {}", booking.getId());
            } catch (Exception e) {
                logger.error("Failed to send departure reminder for booking ID {}", booking.getId(), e);
            }
        }
    }
}
