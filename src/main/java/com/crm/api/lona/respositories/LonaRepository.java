package com.crm.api.lona.respositories;

import com.crm.api.lona.models.Lona;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.util.List;

public interface LonaRepository extends JpaRepository<Lona,Long> {
    @Query(value="SELECT * FROM lona WHERE resulted_at between ?1 AND ?2 ORDER BY CREATED_AT DESC",nativeQuery = true)
    Page<Lona> filterBets(Timestamp start, Timestamp finish, Pageable pageable);

    @Query(value="SELECT * FROM lona WHERE resulted_at between ?1 AND ?2 ORDER BY CREATED_AT DESC",nativeQuery = true)
    List<Lona> filterBetRequests(Timestamp start, Timestamp finish);
}
