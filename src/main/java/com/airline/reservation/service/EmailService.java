package com.airline.reservation.service;

import com.airline.reservation.entity.Booking;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private static final String BASE_URL = "http://localhost:8080";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Async
    public void sendBookingConfirmation(Booking booking) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(booking.getUser().getEmail());
            helper.setFrom("noreply@skyfly.com");
            helper.setSubject("Booking Confirmation - SkyFly Airlines");

            Context context = new Context();
            context.setVariable("userName", booking.getUser().getName());
            context.setVariable("bookingRef", String.format("SKY-%05d", booking.getId()));
            context.setVariable("flightNumber", booking.getFlight().getFlightNumber());
            context.setVariable("airlineName", booking.getFlight().getAirline().getAirlineName());
            context.setVariable("origin", booking.getFlight().getOriginAirport().getAirportCode() + " (" + booking.getFlight().getOriginAirport().getCity() + ")");
            context.setVariable("destination", booking.getFlight().getDestinationAirport().getAirportCode() + " (" + booking.getFlight().getDestinationAirport().getCity() + ")");
            context.setVariable("departureTime", booking.getFlight().getDepartureDateTime().format(DATE_FORMATTER));
            context.setVariable("seatNumbers", booking.getSeatNumbers());
            context.setVariable("cabinClass", booking.getCabinClass());
            context.setVariable("totalFare", String.format("%.2f", booking.getTotalFare()));
            context.setVariable("bookingUrl", BASE_URL + "/user/bookings");

            String htmlContent = templateEngine.process("emails/booking-confirmation", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            logger.info("Sent styled booking confirmation email to {}", booking.getUser().getEmail());

        } catch (MessagingException e) {
            logger.error("Failed to send confirmation email to {}", booking.getUser().getEmail(), e);
        } catch (Exception e) {
            logger.error("Error occurred while sending confirmation email: ", e);
        }
    }

    @Async
    public void sendCheckInReminder(Booking booking) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(booking.getUser().getEmail());
            helper.setFrom("noreply@skyfly.com");
            helper.setSubject("Check-In Open: Your SkyFly Flight " + booking.getFlight().getFlightNumber());

            Context context = new Context();
            context.setVariable("userName", booking.getUser().getName());
            context.setVariable("bookingRef", String.format("SKY-%05d", booking.getId()));
            context.setVariable("flightNumber", booking.getFlight().getFlightNumber());
            context.setVariable("airlineName", booking.getFlight().getAirline().getAirlineName());
            context.setVariable("origin", booking.getFlight().getOriginAirport().getAirportCode() + " (" + booking.getFlight().getOriginAirport().getCity() + ")");
            context.setVariable("destination", booking.getFlight().getDestinationAirport().getAirportCode() + " (" + booking.getFlight().getDestinationAirport().getCity() + ")");
            context.setVariable("departureTime", booking.getFlight().getDepartureDateTime().format(DATE_FORMATTER));
            context.setVariable("seatNumbers", booking.getSeatNumbers());
            context.setVariable("checkInUrl", BASE_URL + "/user/checkin/" + booking.getId());

            String htmlContent = templateEngine.process("emails/checkin-reminder", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            logger.info("Sent check-in reminder email to {}", booking.getUser().getEmail());

        } catch (Exception e) {
            logger.error("Error occurred while sending check-in reminder email: ", e);
        }
    }

    @Async
    public void sendPasswordResetEmail(String email, String resetUrl) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setFrom("noreply@skyfly.com");
            helper.setSubject("Password Reset - SkyFly Airlines");
            
            String htmlContent = "<h2>Password Reset Request</h2>"
                    + "<p>We received a request to reset your password.</p>"
                    + "<p>Click the link below to set a new password:</p>"
                    + "<p><a href='" + resetUrl + "'>" + resetUrl + "</a></p>"
                    + "<p>If you did not request this, please ignore this email.</p>";
            
            helper.setText(htmlContent, true);

            mailSender.send(message);
            logger.info("Sent password reset email to {}", email);

        } catch (Exception e) {
            logger.error("Error occurred while sending password reset email: ", e);
        }
    }
}
