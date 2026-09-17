package com.youssef.eventcheckin.event;


import com.youssef.eventcheckin.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

import static com.youssef.eventcheckin.event.EventStatus.DRAFT;

@Entity
@Getter
@Setter
@Table(name = "events")
@NoArgsConstructor
public class Event {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false,length = 200)
    private String name;
    private String venue;

    private String description;
    @Column(nullable = false)
    private Instant startsAt;
    @Column(nullable = false)
    private Instant endsAt;
    @Column(name = "checkin_opens_at", nullable = false)
    private Instant checkInOpensAt;
    @Column(name = "checkin_closes_at", nullable = false)
    private Instant checkInClosesAt;
    @Column(nullable = false)
    private Integer capacity;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus status = DRAFT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id", nullable = false)
    private User organizer;

    @CreationTimestamp
    @Column(nullable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;
}