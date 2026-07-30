package com.airline.reservation.service;

import com.airline.reservation.entity.Booking;
import com.airline.reservation.entity.Flight;
import com.airline.reservation.entity.FlightStatus;
import com.airline.reservation.entity.TransactionCategory;
import com.airline.reservation.entity.User;
import com.airline.reservation.repository.BookingRepository;
import com.airline.reservation.repository.FlightRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserBookingService {

    private final BookingRepository bookingRepository;
    private final FlightRepository flightRepository;
    private final CouponService couponService;
    private final WalletService walletService;
    private final LoyaltyService loyaltyService;

    public UserBookingService(BookingRepository bookingRepository, FlightRepository flightRepository, CouponService couponService, WalletService walletService, LoyaltyService loyaltyService) {
        this.bookingRepository = bookingRepository;
        this.flightRepository = flightRepository;
        this.couponService = couponService;
        this.walletService = walletService;
        this.loyaltyService = loyaltyService;
    }

    /** Returns all bookings for the given user, newest first. */
    public List<Booking> getBookingsForUser(User user) {
        return bookingRepository.findByUserOrderByBookingDateDesc(user);
    }

    /**
     * Derives realistic four-cabin seat counts from the aircraft's two stored values
     * (businessSeats, economySeats) without changing the Aircraft schema.
     *
     * Proportions:
     *  - First Class     = businessSeats / 4  (min 2)
     *  - Business        = businessSeats - firstClassSeats
     *  - Premium Economy = economySeats  / 4  (min 4)
     *  - Economy         = economySeats  - premiumEconomySeats
     */
    public static class SeatSectionCounts {
        public final int firstClass;
        public final int business;
        public final int premiumEconomy;
        public final int economy;

        public SeatSectionCounts(int firstClass, int business, int premiumEconomy, int economy) {
            this.firstClass = firstClass;
            this.business = business;
            this.premiumEconomy = premiumEconomy;
            this.economy = economy;
        }
    }

    public SeatSectionCounts getSeatSectionCounts(Flight flight) {
        int busTotal = flight.getAircraft() != null ? flight.getAircraft().getBusinessSeats() : 0;
        int ecoTotal = flight.getAircraft() != null
                ? flight.getAircraft().getEconomySeats()
                : flight.getTotalSeats();

        int firstClass    = Math.max(2, busTotal / 4);
        int business      = busTotal - firstClass;
        int premiumEconomy = Math.max(4, ecoTotal / 4);
        int economy       = ecoTotal - premiumEconomy;

        return new SeatSectionCounts(firstClass, business, premiumEconomy, economy);
    }

    /**
     * Returns a list of all seat labels for a flight (e.g. A1, A2 … Z6),
     * divided into four cabin sections: First Class, Business, Premium Economy, Economy.
     * Seat counts are derived via getSeatSectionCounts() — no Aircraft schema changes needed.
     */
    public List<String> generateSeatLabels(Flight flight) {
        SeatSectionCounts counts = getSeatSectionCounts(flight);
        List<String> seats = new ArrayList<>();
        int rowNum = 0;

        // First Class: 4 seats per row (2 + aisle + 2)
        int count = 0;
        while (count < counts.firstClass) {
            char rowChar = (char) ('A' + rowNum);
            for (int col = 1; col <= 4 && count < counts.firstClass; col++, count++) {
                seats.add("" + rowChar + col);
            }
            rowNum++;
        }

        // Business Class: 4 seats per row (2 + aisle + 2)
        count = 0;
        while (count < counts.business) {
            char rowChar = (char) ('A' + rowNum);
            for (int col = 1; col <= 4 && count < counts.business; col++, count++) {
                seats.add("" + rowChar + col);
            }
            rowNum++;
        }

        // Premium Economy: 5 seats per row (2 + aisle + 3)
        count = 0;
        while (count < counts.premiumEconomy) {
            char rowChar = (char) ('A' + rowNum);
            for (int col = 1; col <= 5 && count < counts.premiumEconomy; col++, count++) {
                seats.add("" + rowChar + col);
            }
            rowNum++;
        }

        // Economy: 6 seats per row (3 + aisle + 3)
        count = 0;
        while (count < counts.economy) {
            char rowChar = (char) ('A' + rowNum);
            for (int col = 1; col <= 6 && count < counts.economy; col++, count++) {
                seats.add("" + rowChar + col);
            }
            rowNum++;
        }

        return seats;
    }


    /**
     * Returns the set of booked seat labels for a flight
     * (only CONFIRMED bookings count).
     */
    public Set<String> getBookedSeats(Flight flight) {
        List<String> rows = bookingRepository.findConfirmedSeatNumbersByFlight(flight);
        Set<String> booked = new HashSet<>();
        for (String row : rows) {
            if (row != null && !row.isBlank()) {
                for (String s : row.split(",")) {
                    booked.add(s.trim());
                }
            }
        }
        return booked;
    }

    /**
     * Books the given seats for a user on a flight.
     * Validates:
     *  - seat count > 0
     *  - no requested seat is already booked (server-side race-condition guard)
     *  - enough availableSeats remain
     * Returns the saved Booking.
     */
    @Transactional
    public Booking bookSeats(User user, Long flightId, List<String> requestedSeats, String cabinClass, String appliedCouponCode) {
        if (requestedSeats == null || requestedSeats.isEmpty()) {
            throw new IllegalArgumentException("Please select at least one seat.");
        }

        // Re-fetch inside transaction for optimistic lock
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new IllegalArgumentException("Flight not found."));

        // Server-side seat availability re-validation
        Set<String> alreadyBooked = getBookedSeats(flight);
        List<String> conflicts = requestedSeats.stream()
                .filter(alreadyBooked::contains)
                .collect(Collectors.toList());
        if (!conflicts.isEmpty()) {
            throw new IllegalStateException("Seat(s) " + String.join(", ", conflicts) + " were just booked by someone else. Please re-select.");
        }

        if (flight.getAvailableSeats() < requestedSeats.size()) {
            throw new IllegalStateException("Not enough available seats on this flight.");
        }

        // Decrement available seats
        flight.setAvailableSeats(flight.getAvailableSeats() - requestedSeats.size());
        flightRepository.save(flight);

        // Determine multiplier based on cabin class
        double multiplier = 1.0;
        if ("Premium Economy".equalsIgnoreCase(cabinClass)) multiplier = 1.5;
        else if ("Business Class".equalsIgnoreCase(cabinClass)) multiplier = 2.5;
        else if ("First Class".equalsIgnoreCase(cabinClass)) multiplier = 4.0;
        else cabinClass = "Economy"; // default

        // Compute total fare
        Double baseFare = flight.getFare() * multiplier * requestedSeats.size();
        Double taxes = baseFare * 0.18;
        Double convenienceFee = 200.0;
        Double totalFare = baseFare + taxes + convenienceFee;

        String seatNumbersStr = String.join(", ", requestedSeats);

        // Apply coupon if provided
        double discountAmount = 0.0;
        com.airline.reservation.entity.Coupon coupon = null;
        if (appliedCouponCode != null && !appliedCouponCode.isBlank()) {
            java.math.BigDecimal discount = couponService.validateAndCalculateDiscount(appliedCouponCode, user, totalFare);
            discountAmount = discount.doubleValue();
            totalFare -= discountAmount;
            coupon = couponService.findByCode(appliedCouponCode).orElse(null);
        }

        Booking booking = new Booking(user, flight, seatNumbersStr, LocalDateTime.now(), "CONFIRMED", totalFare, cabinClass);
        booking.setDiscountAmount(discountAmount);
        booking.setCouponCode(appliedCouponCode);
        
        booking = bookingRepository.save(booking);

        if (coupon != null) {
            couponService.recordCouponUsage(coupon, user, booking);
        }

        // Award reward points: 2% of final fare, rounded down
        int rewardPoints = walletService.calculateRewardPoints(totalFare);
        if (rewardPoints > 0) {
            walletService.addRewardPoints(user, rewardPoints,
                    "Reward points for booking SKY-" + String.format("%05d", booking.getId()), booking);
        }

        // Award loyalty miles
        loyaltyService.addMilesForBooking(user, totalFare);

        return booking;
    }

    /**
     * Cancels a booking. Enforces ownership: only the booking's owner can cancel.
     * Restores the seats to the flight's availableSeats count.
     */
    @Transactional
    public void cancelBooking(Long bookingId, Long requestingUserId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found."));

        // Security: enforce ownership
        if (!booking.getUser().getId().equals(requestingUserId)) {
            throw new SecurityException("Access denied: you do not own this booking.");
        }

        if (!"CONFIRMED".equals(booking.getStatus())) {
            throw new IllegalStateException("Only CONFIRMED bookings can be cancelled.");
        }

        // Count seats being restored
        long seatCount = Arrays.stream(booking.getSeatNumbers().split(",")).count();

        // Restore seats
        Flight flight = booking.getFlight();
        flight.setAvailableSeats(flight.getAvailableSeats() + (int) seatCount);
        flightRepository.save(flight);

        booking.setStatus("CANCELLED");
        bookingRepository.save(booking);

        // Refund to wallet
        java.math.BigDecimal refundAmount = java.math.BigDecimal.valueOf(booking.getTotalFare());
        walletService.creditWallet(booking.getUser(), refundAmount,
                TransactionCategory.REFUND,
                "Refund for cancelled booking SKY-" + String.format("%05d", booking.getId()), booking);
    }

    /**
     * Checks if a booking is eligible for online check-in.
     * Must be CONFIRMED, NOT_CHECKED_IN, and within 48h to 1h of departure.
     */
    public boolean isEligibleForCheckIn(Booking booking) {
        if (!"CONFIRMED".equals(booking.getStatus())) {
            return false;
        }
        if ("CHECKED_IN".equals(booking.getCheckInStatus())) {
            return false;
        }
        if (booking.getFlight().getFlightStatus() != null && 
           (booking.getFlight().getFlightStatus() == FlightStatus.CANCELLED || booking.getFlight().getFlightStatus() == FlightStatus.LANDED)) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime departure = booking.getFlight().getDepartureDateTime();
        return now.isAfter(departure.minusHours(48)) && now.isBefore(departure.minusHours(1));
    }

    /**
     * Processes the check-in. Updates seats if changed, sets checkInStatus to CHECKED_IN.
     */
    @Transactional
    public void processCheckIn(Long bookingId, Long requestingUserId, List<String> requestedSeats) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found."));

        if (!booking.getUser().getId().equals(requestingUserId)) {
            throw new SecurityException("Access denied: you do not own this booking.");
        }

        if (!isEligibleForCheckIn(booking)) {
            throw new IllegalStateException("Booking is not eligible for check-in at this time.");
        }

        if (requestedSeats == null || requestedSeats.isEmpty()) {
            throw new IllegalArgumentException("Please select your seat(s).");
        }

        // Check if seats changed
        List<String> currentSeats = Arrays.asList(booking.getSeatNumbers().split(",\\s*"));
        if (currentSeats.size() != requestedSeats.size()) {
            throw new IllegalArgumentException("You must select exactly " + currentSeats.size() + " seat(s).");
        }

        // If they chose different seats, we need to validate them
        boolean seatsChanged = !new HashSet<>(currentSeats).equals(new HashSet<>(requestedSeats));
        if (seatsChanged) {
            Flight flight = booking.getFlight();
            Set<String> alreadyBooked = getBookedSeats(flight);
            
            // Remove current user's old seats from "alreadyBooked" so they can theoretically swap
            currentSeats.forEach(alreadyBooked::remove);

            List<String> conflicts = requestedSeats.stream()
                    .filter(alreadyBooked::contains)
                    .collect(Collectors.toList());
            if (!conflicts.isEmpty()) {
                throw new IllegalStateException("Seat(s) " + String.join(", ", conflicts) + " are already booked. Please re-select.");
            }

            booking.setSeatNumbers(String.join(", ", requestedSeats));
        }

        booking.setCheckInStatus("CHECKED_IN");
        bookingRepository.save(booking);
    }
}
