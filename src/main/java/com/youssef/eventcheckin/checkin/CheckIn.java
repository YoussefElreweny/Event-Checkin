package com.youssef.eventcheckin.checkin;

import com.youssef.eventcheckin.ticket.Ticket;
import com.youssef.eventcheckin.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "check_ins")
public class CheckIn {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = 50)
    private String gate;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket  ticket;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checked_in_by",nullable = false)
    private User checkedInBy;


    @Column(nullable = false)
    @CreationTimestamp
    private Instant checkedInAt;

}