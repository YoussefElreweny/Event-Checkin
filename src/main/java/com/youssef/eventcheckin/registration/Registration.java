package com.youssef.eventcheckin.registration;


import com.youssef.eventcheckin.attendee.Attendee;
import com.youssef.eventcheckin.event.Event;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.Instant;
import java.util.UUID;

import static com.youssef.eventcheckin.registration.RegistrationStatus.Confirmed;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "registrations")
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendee_id", nullable = false)
    private Attendee attendee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RegistrationStatus status = Confirmed;

    @Column(nullable = false)
    @CreationTimestamp
    private Instant registeredAt;

    private Instant cancelledAt;
}