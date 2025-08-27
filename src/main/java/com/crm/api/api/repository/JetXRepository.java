package com.crm.api.api.repository;

import com.crm.api.api.models.JetX;
import com.crm.api.api.models.Plagmatic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;

public interface JetXRepository extends JpaRepository<JetX, Long> {
    @Query(value = "SELECT * FROM jetx_bets WHERE user_id = ?1 ORDER BY created_at DESC", nativeQuery = true)
    Page<JetX> findByUser(long userId, Pageable pageable);

    @Query(value = "SELECT * FROM jetx_bets WHERE created_at BETWEEN ?1 and ?2", nativeQuery = true)
    Page<JetX> findByDate(Pageable pageable, Timestamp start, Timestamp finish);

    @Query(value = "SELECT * FROM jetx_bets ORDER BY created_at DESC", nativeQuery = true)
    Page<JetX> findBets(Pageable pageable);
}
