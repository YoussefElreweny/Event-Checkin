-- Event Check-In System — initial schema
-- Location: src/main/resources/db/migration/V1__initial_schema.sql
-- Requires PostgreSQL 13+ (gen_random_uuid() is in core from 13 onward).
--
-- Naming conventions used throughout:
--   pk_<table>              primary key
--   fk_<table>_<column>     foreign key
--   uk_<table>_<columns>    unique constraint
--   ck_<table>_<rule>       check constraint
--   idx_<table>_<columns>   plain index
-- Named constraints are not decoration: when the database rejects a write,
-- the exception carries this name, and that is how the service layer decides
-- which error to map to which HTTP status.

-- ---------------------------------------------------------------------------
-- users — people who log in (organizers, door staff, admins)
-- ---------------------------------------------------------------------------
CREATE TABLE users (
                       id            UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
                       email         VARCHAR(255) NOT NULL,
                       password_hash VARCHAR(100) NOT NULL,
                       full_name     VARCHAR(150) NOT NULL,
                       role          VARCHAR(20)  NOT NULL,
                       created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
                       updated_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),

                       CONSTRAINT uk_users_email UNIQUE (email),
                       CONSTRAINT ck_users_role  CHECK (role IN ('ADMIN', 'ORGANIZER', 'STAFF'))
);

-- ---------------------------------------------------------------------------
-- events — owned by exactly one organizer
-- ---------------------------------------------------------------------------
CREATE TABLE events (
                        id                UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
                        organizer_id      UUID         NOT NULL,
                        name              VARCHAR(200) NOT NULL,
                        description       TEXT,
                        venue             VARCHAR(255),
                        starts_at         TIMESTAMPTZ  NOT NULL,
                        ends_at           TIMESTAMPTZ  NOT NULL,
                        checkin_opens_at  TIMESTAMPTZ  NOT NULL,
                        checkin_closes_at TIMESTAMPTZ  NOT NULL,
                        capacity          INTEGER      NOT NULL,
                        status            VARCHAR(20)  NOT NULL DEFAULT 'DRAFT',
                        created_at        TIMESTAMPTZ  NOT NULL DEFAULT now(),
                        updated_at        TIMESTAMPTZ  NOT NULL DEFAULT now(),

                        CONSTRAINT fk_events_organizer FOREIGN KEY (organizer_id)
                            REFERENCES users (id) ON DELETE RESTRICT,
                        CONSTRAINT ck_events_status   CHECK (status IN ('DRAFT', 'PUBLISHED', 'CANCELLED', 'COMPLETED')),
                        CONSTRAINT ck_events_capacity CHECK (capacity > 0),
                        CONSTRAINT ck_events_window   CHECK (ends_at > starts_at
                            AND checkin_closes_at > checkin_opens_at)
);

CREATE INDEX idx_events_organizer_id ON events (organizer_id);
CREATE INDEX idx_events_starts_at    ON events (starts_at);

-- ---------------------------------------------------------------------------
-- event_staff — which users may scan tickets at which event
-- Composite primary key: the pair IS the identity. A surrogate id would allow
-- the same (event, user) pair to be inserted twice unless you added a unique
-- constraint on top of it anyway.
-- ---------------------------------------------------------------------------
CREATE TABLE event_staff (
                             event_id    UUID        NOT NULL,
                             user_id     UUID        NOT NULL,
                             assigned_at TIMESTAMPTZ NOT NULL DEFAULT now(),

                             CONSTRAINT pk_event_staff PRIMARY KEY (event_id, user_id),
                             CONSTRAINT fk_event_staff_event FOREIGN KEY (event_id)
                                 REFERENCES events (id) ON DELETE CASCADE,
                             CONSTRAINT fk_event_staff_user FOREIGN KEY (user_id)
                                 REFERENCES users (id) ON DELETE CASCADE
);

-- The composite PK indexes (event_id, user_id) — good for "who staffs event X".
-- This index covers the other direction: "which events does user Y staff".
CREATE INDEX idx_event_staff_user_id ON event_staff (user_id);

-- ---------------------------------------------------------------------------
-- attendees — people who attend. Deliberately NOT users: they never log in.
-- ---------------------------------------------------------------------------
CREATE TABLE attendees (
                           id         UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
                           email      VARCHAR(255) NOT NULL,
                           full_name  VARCHAR(150) NOT NULL,
                           phone      VARCHAR(30),
                           created_at TIMESTAMPTZ  NOT NULL DEFAULT now(),

                           CONSTRAINT uk_attendees_email UNIQUE (email)
);

-- ---------------------------------------------------------------------------
-- registrations — one attendee's place at one event
-- ---------------------------------------------------------------------------
CREATE TABLE registrations (
                               id            UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                               event_id      UUID        NOT NULL,
                               attendee_id   UUID        NOT NULL,
                               status        VARCHAR(20) NOT NULL DEFAULT 'CONFIRMED',
                               registered_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                               cancelled_at  TIMESTAMPTZ,

                               CONSTRAINT fk_registrations_event FOREIGN KEY (event_id)
                                   REFERENCES events (id) ON DELETE CASCADE,
                               CONSTRAINT fk_registrations_attendee FOREIGN KEY (attendee_id)
                                   REFERENCES attendees (id) ON DELETE RESTRICT,
    -- The rule "you cannot register twice for the same event", enforced by the
    -- database rather than by an if-statement that two threads can both pass.
                               CONSTRAINT uk_registrations_event_attendee UNIQUE (event_id, attendee_id),
                               CONSTRAINT ck_registrations_status CHECK (status IN ('CONFIRMED', 'CANCELLED', 'WAITLISTED'))
);

-- uk_registrations_event_attendee already indexes (event_id, attendee_id),
-- which also serves lookups by event_id alone (leftmost-prefix rule).
-- So only the attendee-first direction needs its own index.
CREATE INDEX idx_registrations_attendee_id ON registrations (attendee_id);

-- ---------------------------------------------------------------------------
-- tickets — exactly one per registration; ticket_code is what the QR encodes
-- ---------------------------------------------------------------------------
CREATE TABLE tickets (
                         id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                         registration_id UUID        NOT NULL,
                         ticket_code     VARCHAR(64) NOT NULL,
                         issued_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
                         revoked_at      TIMESTAMPTZ,

                         CONSTRAINT fk_tickets_registration FOREIGN KEY (registration_id)
                             REFERENCES registrations (id) ON DELETE CASCADE,
    -- Enforces the 1:1 with registrations.
                         CONSTRAINT uk_tickets_registration_id UNIQUE (registration_id),
    -- Generated randomly in Java, never derived from the id. Sequential codes
    -- would let anyone guess another attendee's ticket.
    -- Note: UNIQUE creates a btree index automatically. Do not add another.
                         CONSTRAINT uk_tickets_code UNIQUE (ticket_code)
);

-- ---------------------------------------------------------------------------
-- check_ins — the heart of the system
-- ---------------------------------------------------------------------------
CREATE TABLE check_ins (
                           id            UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                           ticket_id     UUID        NOT NULL,
                           checked_in_by UUID        NOT NULL,
                           checked_in_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                           gate          VARCHAR(50),

                           CONSTRAINT fk_check_ins_ticket FOREIGN KEY (ticket_id)
                               REFERENCES tickets (id) ON DELETE CASCADE,
                           CONSTRAINT fk_check_ins_user FOREIGN KEY (checked_in_by)
                               REFERENCES users (id) ON DELETE RESTRICT,
    -- THE business rule of this entire project. One ticket, one entry.
    -- Two scanners hitting the same code in the same millisecond both pass any
    -- read-then-write check in the service layer; exactly one survives this.
    -- The loser raises a constraint violation, which the service maps to 409.
                           CONSTRAINT uk_check_ins_ticket_id UNIQUE (ticket_id)
);

CREATE INDEX idx_check_ins_checked_in_by ON check_ins (checked_in_by);