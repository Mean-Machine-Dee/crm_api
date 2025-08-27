package com.crm.api.sdk.repositories;

import com.crm.api.sdk.entities.SrSport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SrSportRepository extends JpaRepository<SrSport,Long> {
    @Query(value = "SELECT * FROM sr_sports ORDER BY id ASC", nativeQuery = true)
    List<SrSport> findSports();
}
