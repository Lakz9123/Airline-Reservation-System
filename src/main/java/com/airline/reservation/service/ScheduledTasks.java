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
    private final LoyaltyService loyaltyService;
    private final com.airline.reservation.repository.FlightRepository flightRepository;

    public ScheduledTasks(BookingRepository bookingRepository,
                          EmailService emailService,
                          NotificationService notificationService,
                          com.airline.reservation.repository.NotificationRepository notificationRepository,
                          LoyaltyService loyaltyService,
                          com.airline.reservation.repository.FlightRepository flightRepository) {
        this.bookingRepository = bookingRepository;
        this.emailService = emailService;
        this.notificationService = notificationService;
        this.notificationRepository = notificationRepository;
        this.loyaltyService = loyaltyService;
        this.flightRepository = flightRepository;
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

    /**
     * Runs daily at midnight to check for any loyalty tier downgrades.
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void processLoyaltyTierDowngrades() {
        logger.info("Running daily loyalty tier downgrade check.");
        try {
            loyaltyService.processTierDowngrades();
            logger.info("Completed daily loyalty tier downgrade check.");
        } catch (Exception e) {
            logger.error("Failed to process loyalty tier downgrades.", e);
        }
    }

    /**
     * Runs every minute to update the status of active flights automatically.
     * Transitions: SCHEDULED -> BOARDING (45 mins prior) -> DEPARTED (at departure) -> LANDED (at arrival)
     */
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void updateFlightStatuses() {
        LocalDateTime now = LocalDateTime.now();
        List<com.airline.reservation.entity.Flight> activeFlights = flightRepository.findByFlightStatusNot(com.airline.reservation.entity.FlightStatus.LANDED);
        
        for (com.airline.reservation.entity.Flight flight : activeFlights) {
            if (flight.getFlightStatus() == com.airline.reservation.entity.FlightStatus.CANCELLED) {
                continue;
            }
            
            boolean updated = false;
            
            // Check if landed
            if (now.isAfter(flight.getArrivalDateTime()) || now.isEqual(flight.getArrivalDateTime())) {
                flight.setFlightStatus(com.airline.reservation.entity.FlightStatus.LANDED);
                updated = true;
            }
            // Check if departed
            else if (now.isAfter(flight.getDepartureDateTime()) || now.isEqual(flight.getDepartureDateTime())) {
                if (flight.getFlightStatus() != com.airline.reservation.entity.FlightStatus.DEPARTED) {
                    flight.setFlightStatus(com.airline.reservation.entity.FlightStatus.DEPARTED);
                    updated = true;
                }
            }
            // Check if boarding
            else if (now.isAfter(flight.getDepartureDateTime().minusMinutes(45)) || now.isEqual(flight.getDepartureDateTime().minusMinutes(45))) {
                if (flight.getFlightStatus() == com.airline.reservation.entity.FlightStatus.SCHEDULED) {
                    flight.setFlightStatus(com.airline.reservation.entity.FlightStatus.BOARDING);
                    updated = true;
                }
            }
            
            if (updated) {
                flightRepository.save(flight);
                logger.info("Automatically updated flight {} to status {}", flight.getFlightNumber(), flight.getFlightStatus());
            }
        }
    }
}
