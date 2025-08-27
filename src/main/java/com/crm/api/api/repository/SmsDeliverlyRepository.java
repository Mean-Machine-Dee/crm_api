package com.crm.api.api.repository;

import com.crm.api.api.models.SmsDelivery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.util.List;

public interface SmsDeliverlyRepository extends JpaRepository<SmsDelivery, Long> {

    @Query(value = "SELECT * FROM s_m_s_deliverlies WHERE origin =?1 AND created_at BETWEEN ?2 AND ?3 ORDER BY created_at DESC", nativeQuery = true)
    Page<SmsDelivery> getSmses(String origin,Timestamp start, Timestamp end, Pageable pageable);

    @Query(value = "SELECT COUNT(*) FROM s_m_s_deliverlies WHERE  origin =?1 AND created_at BETWEEN ?2 AND ?3", nativeQuery = true)
//    Long countByOriginAndCreatedOnBetween(String origin,Timestamp start, Timestamp end);
    Long aggregateSmses(String origin,Timestamp start, Timestamp end);
}
