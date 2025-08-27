package com.crm.api.sdk.repositories;

import com.crm.api.sdk.entities.SrCompetition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.List;

public interface SrCompetitionRepository extends JpaRepository<SrCompetition, Long> {

    @Query(value = "select * from `sr_competitions` where has_markets = 1 and `sr_tournament_id` in ?1 and `scheduled` >= ?2 order by scheduled asc", nativeQuery = true)
    Page<SrCompetition> findByTournamentIdList(List<Long> ids, Timestamp now, Pageable pageable);
    @Modifying
    @Transactional
    @Query(value = "UPDATE sr_competitions SET is_highlighted = ?1 WHERE id =?2",nativeQuery = true)
    void updateGame(long priority,long gameId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE sr_competitions SET is_highlighted = ?1 WHERE id IN ?1",nativeQuery = true)
    void updateHighlights(List<Long> ids);

    Page<SrCompetition> findByNameLike(String country, Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = "UPDATE sr_competitions SET is_jackpot = 1 WHERE id IN ?1",nativeQuery = true)
    void createJackpot(List<Long> id);

    @Query(value = "select * from `sr_competitions` where id = ?1", nativeQuery = true)
    SrCompetition findGame(long id);


    @Modifying
    @Transactional
    @Query(value = "UPDATE sr_competitions SET is_highlighted = ?1 WHERE id = ?2",nativeQuery = true)
    void highlightSingle(int priority, long id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE sr_competitions SET priority = ?1 WHERE id = ?2",nativeQuery = true)
    void updateFeatured(int priority, long id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE sr_competitions SET is_highlighted = ?1 WHERE id = ?2",nativeQuery = true)
    void updateHighlighted(int priority, long id);


    @Query(value = "select * from `sr_competitions` where has_markets = 1 and `sr_tournament_id`= ?1 and `scheduled` >= ?2 order by scheduled asc", nativeQuery = true)
    List<SrCompetition> findByTournamentId(long id, Timestamp now);
}
