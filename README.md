# Siemens Trains — Backend

REST API for a train booking and scheduling system built with Spring Boot 3.

---

## Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Domain Model](#domain-model)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Environment Variables](#environment-variables)
- [Running Locally](#running-locally)
- [API Reference](#api-reference)
- [Security Model](#security-model)
- [Caching](#caching)
- [Email Notifications](#email-notifications)
- [Testing](#testing)
- [Database Migrations](#database-migrations)

---

## Overview

The system allows customers to search for train journeys (direct or with one changeover), make bookings, and receive email confirmations. Admins can manage trains, routes, and schedules, and record real-time delays — which automatically trigger email notifications to affected passengers.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Runtime | Java 17 |
| Framework | Spring Boot 3.2 |
| Security | Spring Security + JWT (JJWT 0.12) |
| Persistence | Spring Data JPA + PostgreSQL |
| Migrations | Flyway |
| Caching | Spring Cache + Redis (Lettuce) |
| Email | Spring Mail (Gmail SMTP) |
| API Docs | SpringDoc OpenAPI (Swagger UI) |
| Build | Gradle 8 |
| Observability | Spring Actuator |

---

## Domain Model

```
Station
  └── named stop (city, name)

Route
  └── ordered list of RouteStops
        └── each stop: Station + expectedTime + actualTime

Train
  └── belongs to one Route
  └── has capacity (seats)

Schedule
  └── belongs to one Train
  └── set of DayOfWeek (which days it runs)

Booking
  └── belongs to one Schedule
  └── holds: customer info, travelDate, seats, from/to station, status
```

**Relationships:**
- A `Route` is a template (ordered station path).
- A `Train` runs a specific `Route`.
- A `Schedule` defines which days of the week that train runs.
- A `Booking` is a passenger reservation on a specific `Schedule` for a specific `travelDate`.

---

## Project Structure

```
src/main/java/org/example/
├── Main.java                          # @SpringBootApplication entry point
├── auth/
│   ├── controller/AuthController      # POST /api/auth/login
│   ├── dto/                           # LoginRequest, JwtResponse
│   ├── entity/                        # User, Role (ADMIN | CUSTOMER)
│   ├── repository/UserRepository
│   ├── security/                      # JwtUtils, JwtFilter, SecurityConfig
│   └── service/                       # AuthService, UserDetailsServiceImpl
├── train/
│   ├── controller/TrainController     # CRUD /api/trains
│   ├── dto/TrainDto, DeleteResult
│   ├── entity/Train
│   ├── repository/TrainRepository
│   └── service/TrainService           # @Cacheable on reads
├── route/
│   ├── entity/Route
│   ├── dto/RouteDto
│   └── repository/RouteRepository
├── routestop/
│   ├── entity/RouteStop               # stop_order, expectedTime, actualTime
│   ├── dto/RouteStopDto
│   └── repo/RouteStopRepository
├── station/
│   ├── entity/Station
│   └── repository/StationRepository
├── schedule/
│   ├── entity/Schedule                # set<DayOfWeek> days
│   ├── dto/ScheduleDto
│   └── repo/ScheduleRepository
├── booking/
│   ├── controller/BookingController   # POST/DELETE /api/bookings
│   ├── dto/                           # BookingRequestDto, BookingResponseDto
│   ├── entity/Booking
│   ├── repo/BookingRepository
│   └── service/BookingService         # overbooking guard, email on create
├── admin/
│   ├── controller/AdminController     # /api/admin/** (ADMIN only)
│   └── service/AdminService           # delay update + passenger notification
├── infrastructure/
│   ├── controller/SearchController    # GET /api/search
│   ├── exceptions/                    # NotFoundException, OverBookingException, GlobalExceptionHandler
│   └── services/
│       ├── SearchService              # direct + one-changeover journey finder
│       └── EmailService              # booking confirmation + delay notification
└── journey/
    ├── JourneyOptionDto               # list of legs, overall departure/arrival
    └── JourneyLegDto                  # single train leg
```

---

## Prerequisites

- Java 17+
- PostgreSQL 14+
- Redis 6+
- A Gmail account (or any SMTP server) for email
- Gradle (or use the included `./gradlew`)

---

## Environment Variables

Create a `.env` file in the project root (loaded automatically by `spring-dotenv`):

```env
# Database
DB_USERNAME=your_postgres_user
DB_PASSWORD=your_postgres_password

# JWT
JWT_SECRET=a-random-string-at-least-32-chars-long
JWT_EXPIRATION=86400        # seconds (86400 = 24 hours)

# Email (Gmail example)
MAIL_USERNAME=your@gmail.com
MAIL_PASSWORD=your_app_password
```

> For Gmail, generate an **App Password** (Google Account → Security → 2-Step Verification → App passwords). Do not use your regular account password.

The `application.properties` connects these values:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/trains
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

spring.data.redis.host=localhost
spring.data.redis.port=6379

spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${MAIL_USERNAME}
spring.mail.password=${MAIL_PASSWORD}
```

---

## Running Locally

**1. Create the database**

```sql
CREATE DATABASE trains;
```

**2. Start Redis**

```bash
redis-server
```

**3. Set environment variables**

Create the `.env` file as shown above.

**4. Run the app**

```bash
./gradlew bootRun
```

The server starts on `http://localhost:8080`.

**Swagger UI** is available at:
```
http://localhost:8080/swagger-ui.html
```

---

## API Reference

### Auth

| Method | Path | Body | Auth | Description |
|---|---|---|---|---|
| POST | `/api/auth/login` | `{username, password}` | None | Returns JWT token + role |

**Login response:**
```json
{ "token": "eyJ...", "role": "CUSTOMER" }
```

Include the token in all subsequent requests:
```
Authorization: Bearer <token>
```

---

### Trains

| Method | Path | Auth | Description |
|---|---|---|---|
| GET | `/api/trains` | CUSTOMER, ADMIN | List all trains |
| GET | `/api/trains/{id}` | CUSTOMER, ADMIN | Get train by ID |
| POST | `/api/trains` | ADMIN | Create a train |
| PUT | `/api/trains/{id}` | ADMIN | Update a train |
| DELETE | `/api/trains/{id}` | ADMIN | Delete a train |
| DELETE | `/api/trains` | ADMIN | Delete all trains |

**TrainDto:**
```json
{ "id": 1, "name": "IC 123", "capacity": 200, "routeId": 1 }
```

---

### Bookings

| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/api/bookings` | Authenticated | Create a booking |
| DELETE | `/api/bookings/{id}` | Authenticated | Cancel a booking |

**BookingRequestDto:**
```json
{
  "customerName": "Alice",
  "customerEmail": "alice@example.com",
  "scheduleId": 1,
  "travelDate": "2026-06-15",
  "numberOfSeats": 2,
  "fromStation": "Bucharest",
  "toStation": "Cluj"
}
```

The booking is rejected with `409 Conflict` if `bookedSeats + numberOfSeats > trainCapacity`.

---

### Journey Search

| Method | Path | Auth | Description |
|---|---|---|---|
| GET | `/api/search` | Authenticated | Find journeys between two stations |

**Parameters:** `departure`, `arrival`, `date` (ISO date, e.g. `2026-06-15`)

The search returns direct routes and routes with one changeover, filtered to schedules running on the requested day of week.

**Response:**
```json
[
  {
    "legs": [
      {
        "trainName": "IC 123",
        "fromStation": "Bucharest",
        "toStation": "Cluj",
        "departure": "08:00",
        "arrival": "12:30"
      }
    ],
    "totalDeparture": "08:00",
    "totalArrival": "12:30"
  }
]
```

---

### Admin

| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/api/admin/trains` | ADMIN | Create a train |
| DELETE | `/api/admin/trains/{id}` | ADMIN | Delete a train |
| PUT | `/api/admin/route-stops/{id}/actual-time` | ADMIN | Record actual arrival time for a stop |
| GET | `/api/admin/bookings` | ADMIN | List bookings by schedule + date |

Recording an actual time on a route-stop:
- Calculates the delay in minutes (`actualTime - expectedTime`).
- Finds all passengers booked on affected schedules for today.
- Sends a delay notification email to each.

---

## Security Model

Spring Security with stateless JWT. The `JwtFilter` extracts and validates the token on every request before it reaches any controller.

| Role | Access |
|---|---|
| None (public) | `/api/auth/**`, Swagger UI, Actuator |
| CUSTOMER | GET `/api/trains/**`, `/api/search`, `/api/bookings` (POST/DELETE) |
| ADMIN | Everything above + all write operations + `/api/admin/**` |

Roles are stored in the `users` table as an enum (`ADMIN`, `CUSTOMER`) and embedded in the JWT claim `role`.

---

## Caching

Redis is used to cache train data via Spring's `@Cacheable` / `@CacheEvict`:

- `getAll()` → cached under key `"trains::all"`
- `getById(id)` → cached under key `"trains::<id>"`
- Any write (create, update, delete) → evicts all entries in the `"trains"` cache

To inspect the cache:
```bash
redis-cli KEYS "trains*"
```

---

## Email Notifications

Two kinds of email are sent automatically:

| Trigger | Recipient | Content |
|---|---|---|
| Booking created | Customer | Confirmation with route, date, seat count |
| Delay recorded (`PUT /api/admin/route-stops/{id}/actual-time`) | All passengers on affected trains today | Delay duration and station name |

SMTP is configured for Gmail with STARTTLS. Change `spring.mail.host` and `spring.mail.port` for other providers.

---

## Testing

Tests use H2 in-memory database (no PostgreSQL or Redis required). Run all tests:

```bash
./gradlew test
```

Test reports: `build/reports/tests/test/index.html`

### Test layers

| Class | Type | What it covers |
|---|---|---|
| `BookingServiceTest` | Unit (Mockito, no Spring) | Booking creation, overbooking guard, cancellation |
| `TrainControllerTest` | `@WebMvcTest` slice | All train CRUD endpoints — status codes, JSON shape, error responses |
| `BookingRepositoryTest` | `@DataJpaTest` slice | `findByScheduleAndTravelDate`, `countBySchedule` against real H2 schema |

**Test profile** (`application-test.properties`) switches:
- PostgreSQL → H2 in-memory (`MODE=PostgreSQL`)
- Flyway → disabled (H2 uses `ddl-auto=create-drop`)
- Redis → disabled (cache autoconfiguration excluded)
- SMTP → localhost stub

---

## Database Migrations

Managed by Flyway. Migration files live in `src/main/resources/db/migration/`.

| File | Description |
|---|---|
| `V1__init.sql` | Creates all tables: stations, routes, route_stops, trains, schedules, schedule_days, bookings |

To add a schema change, create `V2__description.sql` — Flyway applies it automatically on the next startup.

```
spring.jpa.hibernate.ddl-auto=validate
```

Hibernate validates the schema against entities on startup but never modifies it. All changes go through Flyway scripts.
