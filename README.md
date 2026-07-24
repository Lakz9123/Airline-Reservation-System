# ✈️ SkyFly Airline Reservation System — Phase 1: Admin Module Only

A full-stack **Airline Reservation System (Phase 1: Admin Module)** built with **Java Spring Boot**, **Spring Security**, **Spring Data JPA**, **MySQL**, and **Thymeleaf** templates.

---

## 🚀 Phase 1 Scope & Features (Admin Module Only)

- **Authentication & Security** — Spring Security form-based login enforcing `ROLE_ADMIN` access on all `/admin/**` routes.
- **Admin Dashboard** — Live statistical overview cards displaying:
  - Total Flights Count
  - Total Users Count
  - Total Bookings Count
  - Total Revenue Calculation
- **Flight Management (Full CRUD)** — Create, list, edit, and delete flight schedules with details:
  - Flight Number, Airline Name, Origin, Destination, Departure & Arrival Times, Duration (computed), Fare, Total & Available Seats.
- **Customer Bookings Viewer (Read-only)** — Tabular view of all customer flight reservations, assigned seats, status (`CONFIRMED`/`CANCELLED`), and total fare.
- **User Directory Management** — List registered system users, view roles, and toggle account `Enabled`/`Disabled` status.
- **Data Auto-Seeding** — Seeds default admin (`admin@airline.com` / `admin123`), test users, 5 sample flights, and initial preview bookings on application startup.

---

## 🛠️ Tech Stack

| Layer        | Technology                              |
|--------------|-----------------------------------------|
| Backend      | Java 17, Spring Boot 3.3.1              |
| Security     | Spring Security (Form-based, BCrypt)    |
| Persistence  | Spring Data JPA + Hibernate             |
| Database     | MySQL 8.x                               |
| Frontend     | Thymeleaf + Bootstrap 5                 |
| Build Tool   | Maven 3.9.x                             |

---

## ⚙️ Setup & Execution Instructions

### 1. Prerequisites

- **Java 17** (JDK) — [Download here](https://www.oracle.com/java/technologies/downloads/#java17)
- **MySQL 8.x** — Running on port 3306 with database `airline_db`
- **Maven 3.9+** or included `./mvnw.cmd` wrapper

### 2. Database Setup

Ensure MySQL server is running and create the `airline_db` database:

```sql
CREATE DATABASE IF NOT EXISTS airline_db;
```

Update your connection credentials in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/airline_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=rootpassword
```

### 3. Run the Application

```bash
# Using Maven Wrapper (Windows)
mvnw.cmd spring-boot:run

# Or using Maven CLI
mvn spring-boot:run
```

The application will start at **http://localhost:8080** and automatically redirect to the Admin Portal login.

---

## 🔑 Default Admin Credentials (Seeded on Startup)

| Role  | Email              | Password   | Status  |
|-------|--------------------|------------|---------|
| Admin | `admin@airline.com` | `admin123` | Enabled |

---

## 📁 Project Structure

```
src/
├── main/
│   ├── java/com/airline/reservation/
│   │   ├── AirlineReservationApplication.java   # Main entry point
│   │   ├── config/
│   │   │   └── SecurityConfig.java              # Spring Security config (ROLE_ADMIN guards)
│   │   ├── controller/
│   │   │   ├── AdminController.java             # Admin dashboard, flight CRUD, bookings, users
│   │   │   └── AuthController.java              # Login and root routing
│   │   ├── entity/
│   │   │   ├── Booking.java                     # Booking entity (seatNumbers, totalFare)
│   │   │   ├── Flight.java                      # Flight entity (airlineName, duration, fare)
│   │   │   └── User.java                        # User entity (name, email, enabled)
│   │   ├── repository/
│   │   │   ├── BookingRepository.java
│   │   │   ├── FlightRepository.java
│   │   │   └── UserRepository.java
│   │   ├── service/
│   │   │   ├── BookingService.java              # Booking statistics & listing
│   │   │   ├── FlightService.java               # Flight CRUD & duration logic
│   │   │   ├── UserDetailsServiceImpl.java      # Email-based Spring Security user loader
│   │   │   └── UserService.java                 # User directory & enable/disable toggle
│   │   └── util/
│   │       └── DataLoader.java                  # Seeds initial admin, flights, and bookings
│   └── resources/
│       ├── templates/
│       │   ├── layout.html                      # Admin navbar layout
│       │   ├── login.html                       # Admin email login
│       │   └── admin/
│       │       ├── dashboard.html               # Admin metrics overview
│       │       ├── flights.html                 # Flight management table
│       │       ├── flight-form.html             # Add/Edit flight form
│       │       ├── bookings.html                # Read-only bookings viewer
│       │       └── users.html                   # User directory & toggle status
│       └── application.properties
└── test/
    ├── java/.../AirlineReservationApplicationTests.java
    └── resources/application.properties        # H2 in-memory for testing
```

---

## 🌐 Admin Portal Routes

| Route                  | Access       | Description                                  |
|------------------------|--------------|----------------------------------------------|
| `/`                    | Public       | Redirects to `/admin/dashboard`              |
| `/login`               | Public       | Email & Password Admin login form            |
| `/admin/dashboard`     | ADMIN only   | Stat metrics dashboard                       |
| `/admin/flights`       | ADMIN only   | Flight management list                       |
| `/admin/flights/new`   | ADMIN only   | Create new flight form                       |
| `/admin/flights/edit/{id}` | ADMIN only | Edit flight form                         |
| `/admin/flights/delete/{id}` | ADMIN only | Delete flight                              |
| `/admin/bookings`      | ADMIN only   | Read-only view of customer bookings         |
| `/admin/users`         | ADMIN only   | List users & toggle enabled/disabled status  |
