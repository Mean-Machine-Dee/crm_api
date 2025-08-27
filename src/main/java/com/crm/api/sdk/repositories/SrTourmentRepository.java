package com.crm.api.sdk.repositories;

import com.crm.api.dtos.TournamentSport;
import com.crm.api.payload.requests.SportTournament;
import com.crm.api.sdk.entities.SrCompetition;
import com.crm.api.sdk.entities.SrTournament;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SrTourmentRepository extends JpaRepository<SrTournament, Long> {

    Page<SrTournament> findByNameIgnoreCaseContaining(String tournament, Pageable pageable);

    Page<SrTournament> findByNameLike(String name, Pageable pageable);

    @Query(value = "SELECT id,name,featured FROM sr_tournaments WHERE sr_sport_id = ?1 AND sr_category_id=?2", nativeQuery = true)
    List<TournamentSport> findAllBySportAndCategory(Long sportId, Long categoryId);
}
