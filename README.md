# Event Check-In System

A backend API for running the door at an event: organizers create events and register
attendees, every registration produces a ticket with a unique code, and door staff scan
that code to check people in — exactly once, even when two scanners hit the same ticket
at the same moment.

Built with Java 21, Spring Boot 3, Spring Data JPA, Spring Security, PostgreSQL and Flyway.

---

## Why this exists

Most check-in at small conferences and university events is a printed list and a pen.
That gives you no live headcount, no way to stop one ticket being used by three people,
and no record of who arrived when. This service is the smallest system that fixes those
three things.

## Scope

### In scope (v1)

- Organizer accounts with email/password login (JWT).
- Creating, publishing and cancelling events, each with a capacity and a check-in window.
- Assigning staff members to a specific event; staff can only scan for events they are on.
- Registering attendees for an event, up to capacity, with no duplicate registrations.
- Issuing one ticket per registration, carrying a random, unguessable code.
- Checking in a ticket at the door — idempotent, race-safe, recorded with time, gate and
  the staff member who scanned it.
- Live attendance stats per event.

### Explicitly out of scope

Listed here on purpose. These are the features that would triple the timeline without
teaching anything the in-scope list doesn't already cover.

- Payments, pricing tiers, refunds.
- Seat or table selection.
- Sending email or SMS (ticket codes are returned by the API; delivery is the caller's job).
- QR image generation and a scanner UI — the API takes a code as a string.
- Offline scanning with later sync.
- Multi-tenancy, organizations, teams.
- A frontend of any kind.

## Domain model

| Entity | Meaning |
|---|---|
| `users` | People who log in: `ADMIN`, `ORGANIZER`, `STAFF`. |
| `events` | Owned by one organizer. Has capacity and a check-in window. |
| `event_staff` | Which users may scan at which event. Composite key. |
| `attendees` | People who attend. They never log in. Identified by email. |
| `registrations` | One attendee's place at one event. Unique on (event, attendee). |
| `tickets` | Exactly one per registration. Holds the scannable code. |
| `check_ins` | One row per ticket, ever. The unique constraint is the business rule. |

## API contract

Base path: `/api/v1`. All responses are JSON. Errors use RFC 7807 `application/problem+json`.

### Auth

| Method | Path | Auth | Purpose | Success |
|---|---|---|---|---|
| POST | `/auth/register` | public | Create an organizer account | 201 |
| POST | `/auth/login` | public | Exchange credentials for a JWT | 200 |

### Events

| Method | Path | Auth | Purpose | Success |
|---|---|---|---|---|
| POST | `/events` | ORGANIZER | Create a draft event | 201 |
| GET | `/events` | authenticated | List events, paginated | 200 |
| GET | `/events/{eventId}` | authenticated | Event detail | 200 |
| PATCH | `/events/{eventId}` | owner | Update a draft event | 200 |
| POST | `/events/{eventId}/publish` | owner | Draft → published | 200 |
| POST | `/events/{eventId}/cancel` | owner | Cancel an event | 200 |

### Staff

| Method | Path | Auth | Purpose | Success |
|---|---|---|---|---|
| POST | `/events/{eventId}/staff` | owner | Assign a user as door staff | 201 |
| GET | `/events/{eventId}/staff` | owner | List assigned staff | 200 |
| DELETE | `/events/{eventId}/staff/{userId}` | owner | Unassign | 204 |

### Registrations

| Method | Path | Auth | Purpose | Success |
|---|---|---|---|---|
| POST | `/events/{eventId}/registrations` | owner | Register an attendee, issue a ticket | 201 |
| GET | `/events/{eventId}/registrations` | owner | List registrations, paginated | 200 |
| GET | `/registrations/{registrationId}` | owner | Registration with its ticket code | 200 |
| DELETE | `/registrations/{registrationId}` | owner | Cancel, revoke the ticket | 204 |

### Check-in

| Method | Path | Auth | Purpose | Success |
|---|---|---|---|---|
| POST | `/events/{eventId}/check-ins` | staff on this event | Scan a ticket code | 201 |
| GET | `/events/{eventId}/check-ins` | owner or staff | Check-in log, paginated | 200 |
| GET | `/events/{eventId}/stats` | owner or staff | Registered, checked in, rate | 200 |

`POST /events/{eventId}/check-ins` request body:

```json
{ "ticketCode": "7f3c9a1e4b2d8056", "gate": "MAIN" }
```

### Error contract

The status code is part of the contract. Decide it here, before writing the controller,
and make the integration tests assert it.

| Situation | Status | `type` slug |
|---|---|---|
| Body fails bean validation | 400 | `validation-error` |
| No or invalid JWT | 401 | `unauthenticated` |
| Valid JWT, not your event / not staff here | 403 | `forbidden` |
| Unknown event, registration, or ticket code | 404 | `not-found` |
| Attendee already registered for this event | 409 | `duplicate-registration` |
| Event is at capacity | 409 | `event-full` |
| Ticket already checked in | 409 | `already-checked-in` |
| Ticket revoked, event not published, or outside the check-in window | 422 | `check-in-not-allowed` |

`409 already-checked-in` returns the original check-in time and gate in the problem
detail, so the person at the door can see when the ticket was first used.

## Running locally

```bash
docker compose up -d          # PostgreSQL on 5432
./mvnw spring-boot:run        # Flyway migrates on startup
```

API docs at `http://localhost:8080/swagger-ui.html`.

```bash
./mvnw verify                 # unit + integration tests (Testcontainers)
```

## Design decisions

Longer decision records live in `docs/adr/`.

- The one-check-in-per-ticket rule is a `UNIQUE` constraint on `check_ins.ticket_id`,
  not a service-layer check. A read-then-write check lets two concurrent scans both
  succeed; the constraint makes exactly one win and the loser becomes a 409.
- Attendees are separate from users because attendees never authenticate, and separate
  from registrations because one person attends many events.
- Ticket codes are random, not derived from the primary key. Sequential codes are guessable.
- All timestamps are `TIMESTAMPTZ` stored in UTC and mapped to `java.time.Instant`.
- Enums are persisted as strings (`@Enumerated(EnumType.STRING)`); the JPA default of
  `ORDINAL` silently corrupts historical rows when the enum is reordered.

## Roadmap

- [ ] Phase 1 — project skeleton, Docker Compose, Flyway, health endpoint
- [ ] Phase 2 — entities, repositories, repository tests
- [ ] Phase 3 — events end to end, validation, global exception handler
- [ ] Phase 4 — registrations, tickets, race-safe check-in
- [ ] Phase 5 — JWT auth, roles, ownership rules
- [ ] Phase 6 — test pyramid, OpenAPI, structured logging
- [ ] Phase 7 — Dockerfile, GitHub Actions, deployment
