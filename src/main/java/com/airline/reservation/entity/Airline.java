package com.airline.reservation.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "airlines")
public class Airline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Airline name is required")
    @Column(nullable = false, unique = true)
    private String airlineName;

    @Column(columnDefinition = "TEXT")
    private String logo;

    @NotBlank(message = "Country is required")
    private String country;

    private String website;

    @NotBlank(message = "Support email is required")
    @Email(message = "Support email must be a valid email format")
    private String supportEmail;

    public Airline() {
    }

    public Airline(String airlineName, String logo, String country, String website, String supportEmail) {
        this.airlineName = airlineName;
        this.logo = logo;
        this.country = country;
        this.website = website;
        this.supportEmail = supportEmail;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAirlineName() {
        return airlineName;
    }

    public void setAirlineName(String airlineName) {
        this.airlineName = airlineName;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getSupportEmail() {
        return supportEmail;
    }

    public void setSupportEmail(String supportEmail) {
        this.supportEmail = supportEmail;
    }
}
