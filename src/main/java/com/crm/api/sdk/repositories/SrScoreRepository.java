package com.crm.api.sdk.repositories;

import com.crm.api.api.models.Picks;
import com.crm.api.sdk.entities.SrScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface SrScoreRepository extends JpaRepository<SrScore,Long> {

    @Query(value = "SELECT * FROM sr_scores WHERE sr_competition_id = ?1 LIMIT 1", nativeQuery = true)
    SrScore findByCompetitionId(Long srCompetitionId);
}
