package com.crm.api.crm.repository;

import com.crm.api.crm.models.Jackpot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface JackpotRepository extends JpaRepository<Jackpot,Long> {
    @Query(value = "SELECT * FROM jackpots WHERE status=1 LIMIT 1", nativeQuery = true)
    Jackpot findByActive();
}
