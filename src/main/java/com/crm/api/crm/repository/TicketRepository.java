package com.crm.api.crm.repository;

import com.crm.api.crm.models.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Query(value = "SELECT * FROM tickets ORDER BY created_at ASC", nativeQuery = true)
    Page<Ticket> getPagedTickets(Pageable pageable);

    @Query(value = "SELECT * FROM tickets WHERE created_by = ?1 ORDER BY created_at ASC", nativeQuery = true)
    Page<Ticket> findPagedUserTickets(int id, Pageable pageable);
}
