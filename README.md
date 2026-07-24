# ✈️ SkyFly Airline Reservation System

A full-stack **Airline Reservation System** built with **Java Spring Boot**, **Spring Security**, **Spring Data JPA**, **MySQL**, and **Thymeleaf** templates.

---

## 🚀 Features

- **Authentication** — Form-based login/registration with BCrypt passwords and role-based access (USER / ADMIN)
- **Flight Search** — Search available flights by origin, destination, and departure date
- **Booking** — Book one or more seats on a flight, with real-time seat availability tracking
- **My Bookings** — View all past/active bookings and cancel confirmed bookings (seats are restored)
- **Admin Panel** — Full CRUD management of flights (Create, Read, Update, Delete)
- **Auto-seeded Data** — 1 admin user, 1 test user, and 6 sample flights loaded on startup

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

## ⚙️ Setup Instructions

### 1. Prerequisites

- **Java 17** (JDK) — [Download here](https://www.oracle.com/java/technologies/downloads/#java17)
- **MySQL 8.x** — [Download here](https://dev.mysql.com/downloads/)
- **Maven 3.9+** — [Download here](https://maven.apache.org/download.cgi) (or use the included `mvnw` wrapper)

### 2. Create the MySQL Database

Log into MySQL and run:

```sql
CREATE DATABASE airline_db;
```

### 3. Configure Database Credentials

Open `src/main/resources/application.properties` and update the credentials:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/airline_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

### 4. Run the Application

**Option A — Using Maven Wrapper (recommended, no local Maven needed):**
```bash
./mvnw spring-boot:run        # Linux/Mac
mvnw.cmd spring-boot:run      # Windows
```

**Option B — Using local Maven:**
```bash
mvn spring-boot:run
```

The app will start at **http://localhost:8080**

---

## 🔑 Default Login Credentials (Seeded on Startup)

| Role  | Username | Password  |
|-------|----------|-----------|
| Admin | `admin`  | `admin123` |
| User  | `user`   | `user123`  |

> These accounts are created automatically via `DataLoader.java` if they don't already exist.

---

## 📁 Project Structure

```
src/
├── main/
│   ├── java/com/airline/reservation/
│   │   ├── AirlineReservationApplication.java   # Main entry point
│   │   ├── config/
│   │   │   └── SecurityConfig.java              # Spring Security config
│   │   ├── controller/
│   │   │   ├── AdminController.java             # Admin flight CRUD
│   │   │   ├── AuthController.java              # Login & Registration
│   │   │   ├── BookingController.java           # Booking & Cancellation
│   │   │   └── FlightController.java            # Home & Flight Search
│   │   ├── entity/
│   │   │   ├── Booking.java                     # Booking entity
│   │   │   ├── Flight.java                      # Flight entity
│   │   │   └── User.java                        # User entity
│   │   ├── repository/
│   │   │   ├── BookingRepository.java
│   │   │   ├── FlightRepository.java
│   │   │   └── UserRepository.java
│   │   ├── service/
│   │   │   ├── BookingService.java              # Booking business logic
│   │   │   ├── FlightService.java               # Flight CRUD logic
│   │   │   ├── UserDetailsServiceImpl.java      # Spring Security user loader
│   │   │   └── UserService.java                 # User registration
│   │   └── util/
│   │       └── DataLoader.java                  # Seeds initial data
│   └── resources/
│       ├── templates/
│       │   ├── layout.html                      # Base Thymeleaf layout
│       │   ├── login.html
│       │   ├── register.html
│       │   ├── index.html                       # Home + Search
│       │   ├── admin/
│       │   │   ├── flights.html                 # Admin flight list
│       │   │   └── flight-form.html             # Create/Edit form
│       │   └── user/
│       │       ├── book.html                    # Booking confirmation
│       │       └── bookings.html                # My bookings
│       └── application.properties
└── test/
    ├── java/.../AirlineReservationApplicationTests.java
    └── resources/application.properties        # H2 in-memory for tests
```

---

## 🗃️ Database Schema (auto-created by JPA)

**`users`** — `id`, `username`, `password`, `role`

**`flights`** — `id`, `flight_number`, `origin`, `destination`, `departure_date_time`, `arrival_date_time`, `total_seats`, `available_seats`, `price`

**`bookings`** — `id`, `user_id`, `flight_id`, `number_of_seats`, `booking_date`, `status`

---

## 🌐 Application URLs

| URL                    | Access       | Description                    |
|------------------------|--------------|--------------------------------|
| `/`                    | Public       | Home page & flight search      |
| `/register`            | Public       | User registration              |
| `/login`               | Public       | Login page                     |
| `/search`              | Authenticated| Flight search results          |
| `/booking/book`        | USER/ADMIN   | Book seats on a flight         |
| `/bookings`            | USER/ADMIN   | View & cancel bookings         |
| `/admin/flights`       | ADMIN only   | Flight management dashboard    |
| `/admin/flights/new`   | ADMIN only   | Create new flight              |
| `/admin/flights/edit/{id}` | ADMIN only | Edit existing flight        |

---

## 📝 Notes

- The application uses `spring.jpa.hibernate.ddl-auto=update` — schema is auto-managed by Hibernate
- Tests run with an H2 in-memory database (no MySQL required for testing)
- CSRF is disabled for simplicity in development; enable it for production use
- No payment integration — booking is mocked/confirmed instantly
