package com.youssef.eventcheckin.attendee;

import com.youssef.eventcheckin.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttendeeRepository extends JpaRepository<Attendee, UUID> {

    boolean existsByEmail(String email);

    @Override
    Optional<Attendee> findById(UUID uuid);
}
