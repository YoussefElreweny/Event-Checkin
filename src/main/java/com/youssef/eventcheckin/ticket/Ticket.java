package com.youssef.eventcheckin.ticket;


import com.youssef.eventcheckin.registration.Registration;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "tickets")
public class Ticket {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registration_id")
    private Registration registration;

    @Column(length = 64, nullable = false)
    private String ticketCode;

    @Column(nullable = false)
    @CreationTimestamp
    private Instant issuedAt;

    private Instant revokedAt;


}