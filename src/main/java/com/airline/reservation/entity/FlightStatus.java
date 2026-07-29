package com.airline.reservation.entity;

public enum FlightStatus {
    SCHEDULED("Scheduled", "bg-success"),
    DELAYED("Delayed", "bg-warning text-dark"),
    BOARDING("Boarding", "bg-primary"),
    DEPARTED("Departed", "bg-purple"),
    LANDED("Landed", "bg-secondary"),
    CANCELLED("Cancelled", "bg-danger");

    private final String label;
    private final String badgeClass;

    FlightStatus(String label, String badgeClass) {
        this.label = label;
        this.badgeClass = badgeClass;
    }

    public String getLabel() {
        return label;
    }

    public String getBadgeClass() {
        return badgeClass;
    }
}
