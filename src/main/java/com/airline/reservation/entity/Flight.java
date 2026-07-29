package com.airline.reservation.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "flights")
public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "flight_number", nullable = false)
    private String flightNumber;

    @ManyToOne(optional = false)
    @JoinColumn(name = "airline_id", nullable = false)
    private Airline airline;

    @ManyToOne(optional = false)
    @JoinColumn(name = "origin_airport_id", nullable = false)
    private Airport originAirport;

    @ManyToOne(optional = false)
    @JoinColumn(name = "destination_airport_id", nullable = false)
    private Airport destinationAirport;

    @Column(name = "departure_date_time", nullable = false)
    private LocalDateTime departureDateTime;

    @Column(name = "arrival_date_time", nullable = false)
    private LocalDateTime arrivalDateTime;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column(nullable = false)
    private Double fare;

    @ManyToOne(optional = false)
    @JoinColumn(name = "aircraft_id", nullable = false)
    private Aircraft aircraft;

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

    // Boarding-related fields
    @Column(name = "gate_number")
    private String gateNumber;

    @Column(name = "terminal")
    private String terminal;

    @Column(name = "boarding_zone")
    private String boardingZone;

    @Enumerated(EnumType.STRING)
    @Column(name = "flight_status", nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'SCHEDULED'")
    private FlightStatus flightStatus = FlightStatus.SCHEDULED;

    // Constructors
    public Flight() {}

    public Flight(String flightNumber, Airline airline, Airport originAirport, Airport destinationAirport, LocalDateTime departureDateTime, LocalDateTime arrivalDateTime, Integer durationMinutes, Double fare, Aircraft aircraft, Integer availableSeats) {
        this.flightNumber = flightNumber;
        this.airline = airline;
        this.originAirport = originAirport;
        this.destinationAirport = destinationAirport;
        this.departureDateTime = departureDateTime;
        this.arrivalDateTime = arrivalDateTime;
        this.durationMinutes = durationMinutes;
        this.fare = fare;
        this.aircraft = aircraft;
        this.availableSeats = availableSeats;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public Airline getAirline() {
        return airline;
    }

    public void setAirline(Airline airline) {
        this.airline = airline;
    }

    public Airport getOriginAirport() {
        return originAirport;
    }

    public void setOriginAirport(Airport originAirport) {
        this.originAirport = originAirport;
    }

    public Airport getDestinationAirport() {
        return destinationAirport;
    }

    public void setDestinationAirport(Airport destinationAirport) {
        this.destinationAirport = destinationAirport;
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

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Double getFare() {
        return fare;
    }

    public void setFare(Double fare) {
        this.fare = fare;
    }

    public Aircraft getAircraft() {
        return aircraft;
    }

    public void setAircraft(Aircraft aircraft) {
        this.aircraft = aircraft;
    }

    public Integer getTotalSeats() {
        return aircraft != null ? aircraft.getCapacity() : 0;
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

    public String getGateNumber() {
        return gateNumber;
    }

    public void setGateNumber(String gateNumber) {
        this.gateNumber = gateNumber;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public String getBoardingZone() {
        return boardingZone;
    }

    public void setBoardingZone(String boardingZone) {
        this.boardingZone = boardingZone;
    }

    /** Boarding time = departure minus 45 minutes */
    public LocalDateTime getBoardingTime() {
        return departureDateTime != null ? departureDateTime.minusMinutes(45) : null;
    }

    public FlightStatus getFlightStatus() {
        return flightStatus;
    }

    public void setFlightStatus(FlightStatus flightStatus) {
        this.flightStatus = flightStatus;
    }
}
