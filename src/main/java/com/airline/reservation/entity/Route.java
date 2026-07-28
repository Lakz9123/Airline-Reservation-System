package com.airline.reservation.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "routes")
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "airline_id", nullable = false)
    private Airline airline;

    @ManyToOne(optional = false)
    @JoinColumn(name = "origin_airport_id", nullable = false)
    private Airport originAirport;

    @ManyToOne(optional = false)
    @JoinColumn(name = "destination_airport_id", nullable = false)
    private Airport destinationAirport;

    @Column(name = "distance_miles", nullable = false)
    private Integer distanceMiles;

    @Column(name = "base_fare", nullable = false)
    private Double baseFare;

    @Column(name = "standard_duration_minutes", nullable = false)
    private Integer standardDurationMinutes;

    public Route() {}

    public Route(Airline airline, Airport originAirport, Airport destinationAirport, Integer distanceMiles, Double baseFare, Integer standardDurationMinutes) {
        this.airline = airline;
        this.originAirport = originAirport;
        this.destinationAirport = destinationAirport;
        this.distanceMiles = distanceMiles;
        this.baseFare = baseFare;
        this.standardDurationMinutes = standardDurationMinutes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getDistanceMiles() {
        return distanceMiles;
    }

    public void setDistanceMiles(Integer distanceMiles) {
        this.distanceMiles = distanceMiles;
    }

    public Double getBaseFare() {
        return baseFare;
    }

    public void setBaseFare(Double baseFare) {
        this.baseFare = baseFare;
    }

    public Integer getStandardDurationMinutes() {
        return standardDurationMinutes;
    }

    public void setStandardDurationMinutes(Integer standardDurationMinutes) {
        this.standardDurationMinutes = standardDurationMinutes;
    }
}
