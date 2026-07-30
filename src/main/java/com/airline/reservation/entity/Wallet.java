package com.airline.reservation.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "wallets")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "reward_points", nullable = false)
    private Integer rewardPoints = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(255) default 'SILVER'")
    private LoyaltyTier tier = LoyaltyTier.SILVER;

    @Column(name = "total_miles", nullable = false, columnDefinition = "integer default 0")
    private Integer totalMiles = 0;

    @Column(name = "miles_this_year", nullable = false, columnDefinition = "integer default 0")
    private Integer milesThisYear = 0;

    @Column(name = "tier_valid_until")
    private LocalDate tierValidUntil;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Wallet() {}

    public Wallet(User user) {
        this.user = user;
        this.balance = BigDecimal.ZERO;
        this.rewardPoints = 0;
        this.tier = LoyaltyTier.SILVER;
        this.totalMiles = 0;
        this.milesThisYear = 0;
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public Integer getRewardPoints() { return rewardPoints; }
    public void setRewardPoints(Integer rewardPoints) { this.rewardPoints = rewardPoints; }

    public LoyaltyTier getTier() { return tier; }
    public void setTier(LoyaltyTier tier) { this.tier = tier; }

    public Integer getTotalMiles() { return totalMiles; }
    public void setTotalMiles(Integer totalMiles) { this.totalMiles = totalMiles; }

    public Integer getMilesThisYear() { return milesThisYear; }
    public void setMilesThisYear(Integer milesThisYear) { this.milesThisYear = milesThisYear; }

    public LocalDate getTierValidUntil() { return tierValidUntil; }
    public void setTierValidUntil(LocalDate tierValidUntil) { this.tierValidUntil = tierValidUntil; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
