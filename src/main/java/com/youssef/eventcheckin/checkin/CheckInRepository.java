package com.youssef.eventcheckin.checkin;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CheckInRepository extends JpaRepository<CheckIn, UUID> {

    boolean existsByTicketId(UUID ticketId);

    Page<CheckIn> findByTicket_Registration_Event_Id(UUID eventId, Pageable pageable);

}
