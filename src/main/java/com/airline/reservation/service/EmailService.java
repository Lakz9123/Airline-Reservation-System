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

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendBookingConfirmation(Booking booking) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(booking.getUser().getEmail());
            helper.setSubject("Booking Confirmation - SkyFly Airlines");
            
            String htmlContent = "<h2>Booking Confirmed!</h2>"
                    + "<p>Dear " + booking.getUser().getName() + ",</p>"
                    + "<p>Your booking has been successfully confirmed. Here are the details:</p>"
                    + "<ul>"
                    + "<li><strong>Booking Reference:</strong> SKY-" + String.format("%05d", booking.getId()) + "</li>"
                    + "<li><strong>Flight:</strong> " + booking.getFlight().getFlightNumber() + " (" + booking.getFlight().getAirlineName() + ")</li>"
                    + "<li><strong>Route:</strong> " + booking.getFlight().getOrigin() + " to " + booking.getFlight().getDestination() + "</li>"
                    + "<li><strong>Seats:</strong> " + booking.getSeatNumbers() + "</li>"
                    + "<li><strong>Total Fare:</strong> $" + String.format("%.2f", booking.getTotalFare()) + "</li>"
                    + "</ul>"
                    + "<p>Thank you for flying with us!</p>";
            
            helper.setText(htmlContent, true);

            mailSender.send(message);
            logger.info("Sent booking confirmation email to {}", booking.getUser().getEmail());

        } catch (MessagingException e) {
            logger.error("Failed to send email to {}", booking.getUser().getEmail(), e);
        } catch (Exception e) {
            logger.error("Error occurred while sending email: ", e);
        }
    }

    @Async
    public void sendPasswordResetEmail(String email, String resetUrl) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
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
