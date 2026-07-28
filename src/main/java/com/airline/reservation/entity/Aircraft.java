package com.airline.reservation.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "aircrafts")
public class Aircraft {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "aircraft_name", nullable = false)
    private String aircraftName;

    @Column(name = "aircraft_number", nullable = false, unique = true)
    private String aircraftNumber;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private Integer capacity;

    @Column(name = "business_seats", nullable = false)
    private Integer businessSeats;

    @Column(name = "economy_seats", nullable = false)
    private Integer economySeats;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AircraftStatus status;

    public Aircraft() {
    }

    public Aircraft(String aircraftName, String aircraftNumber, String model, Integer capacity, Integer businessSeats, Integer economySeats, AircraftStatus status) {
        this.aircraftName = aircraftName;
        this.aircraftNumber = aircraftNumber;
        this.model = model;
        this.capacity = capacity;
        this.businessSeats = businessSeats;
        this.economySeats = economySeats;
        this.status = status;
        validateSeats();
    }

    @PrePersist
    @PreUpdate
    private void validateSeats() {
        if (businessSeats != null && economySeats != null && capacity != null) {
            if (businessSeats + economySeats != capacity) {
                throw new IllegalStateException("Business seats + Economy seats must equal Total Capacity");
            }
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAircraftName() {
        return aircraftName;
    }

    public void setAircraftName(String aircraftName) {
        this.aircraftName = aircraftName;
    }

    public String getAircraftNumber() {
        return aircraftNumber;
    }

    public void setAircraftNumber(String aircraftNumber) {
        this.aircraftNumber = aircraftNumber;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Integer getBusinessSeats() {
        return businessSeats;
    }

    public void setBusinessSeats(Integer businessSeats) {
        this.businessSeats = businessSeats;
    }

    public Integer getEconomySeats() {
        return economySeats;
    }

    public void setEconomySeats(Integer economySeats) {
        this.economySeats = economySeats;
    }

    public AircraftStatus getStatus() {
        return status;
    }

    public void setStatus(AircraftStatus status) {
        this.status = status;
    }
}
