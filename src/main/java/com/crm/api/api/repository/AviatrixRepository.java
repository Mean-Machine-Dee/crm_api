package com.crm.api.api.repository;

import com.crm.api.api.models.Aviatrix;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;

public interface AviatrixRepository extends JpaRepository<Aviatrix, Long> {
    @Query(value = "SELECT * FROM aviatrix_bets WHERE user_id = ?1 ORDER BY created_at DESC", nativeQuery = true)
    Page<Aviatrix> findByUserBet(long userId, Pageable pageable);

    @Query(value = "SELECT * FROM aviatrix_bets WHERE created_at BETWEEN ?1 and ?2", nativeQuery = true)
    Page<Aviatrix> findByDate(Pageable pageable, Timestamp start, Timestamp finish);
    @Query(value = "SELECT * FROM aviatrix_bets ORDER BY created_at DESC", nativeQuery = true)
    Page<Aviatrix> findBets(Pageable pageable);
}
