package com.airline.reservation.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "flight_id", nullable = false)
    private Flight flight;

    @Column(name = "seat_numbers", nullable = false)
    private String seatNumbers; // Comma-separated (e.g. "12A, 12B")

    @Column(name = "booking_date", nullable = false)
    private LocalDateTime bookingDate;

    @Column(nullable = false)
    private String status; // "CONFIRMED" or "CANCELLED"

    @Column(name = "total_fare", nullable = false)
    private Double totalFare;

    @Column(name = "cabin_class", nullable = false)
    private String cabinClass;

    @Column(nullable = false)
    private String checkInStatus = "NOT_CHECKED_IN"; // NOT_CHECKED_IN, CHECKED_IN

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean reminderSent = false;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean departureReminderSent = false;

    // Constructors
    public Booking() {}

    public Booking(User user, Flight flight, String seatNumbers, LocalDateTime bookingDate, String status, Double totalFare, String cabinClass) {
        this.user = user;
        this.flight = flight;
        this.seatNumbers = seatNumbers;
        this.bookingDate = bookingDate;
        this.status = status;
        this.totalFare = totalFare;
        this.cabinClass = cabinClass;
        this.checkInStatus = "NOT_CHECKED_IN";
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public String getSeatNumbers() {
        return seatNumbers;
    }

    public void setSeatNumbers(String seatNumbers) {
        this.seatNumbers = seatNumbers;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getTotalFare() {
        return totalFare;
    }

    public void setTotalFare(Double totalFare) {
        this.totalFare = totalFare;
    }

    public String getCabinClass() {
        return cabinClass;
    }

    public void setCabinClass(String cabinClass) {
        this.cabinClass = cabinClass;
    }

    public String getCheckInStatus() {
        return checkInStatus;
    }

    public void setCheckInStatus(String checkInStatus) {
        this.checkInStatus = checkInStatus;
    }

    public boolean isReminderSent() {
        return reminderSent;
    }

    public void setReminderSent(boolean reminderSent) {
        this.reminderSent = reminderSent;
    }

    public boolean isDepartureReminderSent() {
        return departureReminderSent;
    }

    public void setDepartureReminderSent(boolean departureReminderSent) {
        this.departureReminderSent = departureReminderSent;
    }
}
