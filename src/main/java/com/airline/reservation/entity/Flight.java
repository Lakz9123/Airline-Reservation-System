package com.airline.reservation.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "flights")
public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @Column(name = "departure_date_time", nullable = false)
    private LocalDateTime departureDateTime;

    @Column(name = "arrival_date_time", nullable = false)
    private LocalDateTime arrivalDateTime;

    @Column(name = "available_seats", nullable = false)
    private Integer availableSeats;

    @Column(name = "economy_fare")
    private Double economyFare;

    @Column(name = "premium_economy_fare")
    private Double premiumEconomyFare;

    @Column(name = "business_fare")
    private Double businessFare;

    @Column(name = "first_class_fare")
    private Double firstClassFare;

    public Flight() {}

    public Flight(Schedule schedule, LocalDateTime departureDateTime, LocalDateTime arrivalDateTime, Integer availableSeats) {
        this.schedule = schedule;
        this.departureDateTime = departureDateTime;
        this.arrivalDateTime = arrivalDateTime;
        this.availableSeats = availableSeats;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public LocalDateTime getDepartureDateTime() {
        return departureDateTime;
    }

    public void setDepartureDateTime(LocalDateTime departureDateTime) {
        this.departureDateTime = departureDateTime;
    }

    public LocalDateTime getArrivalDateTime() {
        return arrivalDateTime;
    }

    public void setArrivalDateTime(LocalDateTime arrivalDateTime) {
        this.arrivalDateTime = arrivalDateTime;
    }

    public Integer getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(Integer availableSeats) {
        this.availableSeats = availableSeats;
    }

    public Double getEconomyFare() {
        return economyFare;
    }

    public void setEconomyFare(Double economyFare) {
        this.economyFare = economyFare;
    }

    public Double getPremiumEconomyFare() {
        return premiumEconomyFare;
    }

    public void setPremiumEconomyFare(Double premiumEconomyFare) {
        this.premiumEconomyFare = premiumEconomyFare;
    }

    public Double getBusinessFare() {
        return businessFare;
    }

    public void setBusinessFare(Double businessFare) {
        this.businessFare = businessFare;
    }

    public Double getFirstClassFare() {
        return firstClassFare;
    }

    public void setFirstClassFare(Double firstClassFare) {
        this.firstClassFare = firstClassFare;
    }

    // Helper methods for templates
    public String getFlightNumber() {
        return "FL-" + schedule.getId() + "-" + id;
    }

    public Airline getAirline() {
        return schedule.getRoute().getAirline();
    }

    public Aircraft getAircraft() {
        return schedule.getAircraft();
    }

    public Airport getOriginAirport() {
        return schedule.getRoute().getOriginAirport();
    }

    public Airport getDestinationAirport() {
        return schedule.getRoute().getDestinationAirport();
    }

    public Integer getTotalSeats() {
        return schedule.getAircraft() != null ? schedule.getAircraft().getCapacity() : 0;
    }

    public Integer getDurationMinutes() {
        return schedule.getRoute().getStandardDurationMinutes();
    }

    public Double getFare() {
        return economyFare;
    }
}
