package com.crm.api.api.repository;

import com.crm.api.api.models.Bet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;


@Repository
public interface BetRepository extends JpaRepository<Bet, Long> {
    @Query(value="SELECT * FROM bets WHERE created_at BETWEEN ?1 AND ?2", nativeQuery = true)
    List<Bet> getBetsByDate(Timestamp burundiTime,Timestamp stop);



    @Query(value="SELECT * FROM bets WHERE created_at BETWEEN ?1 AND ?2", nativeQuery = true)
    Page<Bet> getPaginatedBetsByDate(Timestamp burundiTime,Timestamp stop, Pageable pageable);


    @Query(value="SELECT * FROM bets WHERE won = 1 AND created_at BETWEEN ?1 AND ?2 LIMIT 1000", nativeQuery = true)
    List<Bet> getWonBetsByDate(Timestamp burundiTime,Timestamp stop);

    @Query(value="SELECT * FROM bets WHERE won = 1 AND iso = ?1 AND created_at BETWEEN ?2 AND ?3 LIMIT 1000", nativeQuery = true)
    List<Bet> getWonBetsByDateAndCountry(String iso, Timestamp burundiTime,Timestamp stop);


    @Query(value="SELECT COUNT(id) FROM bets WHERE status = ?1", nativeQuery = true)
    Integer activeBets(boolean status);

    @Query(value="SELECT * FROM bets WHERE user_id = ?1 ORDER BY created_at DESC", nativeQuery = true)
    List<Bet> getClientBets(int id);

    @Query(value="SELECT * FROM bets WHERE user_id = ?1 ORDER BY created_at DESC", nativeQuery = true)
    Page<Bet> getPagedClientBets(long id, Pageable page);


    @Query(value="SELECT * FROM bets WHERE type =?1  ORDER BY created_at DESC", nativeQuery = true)
    Page<Bet> getPaginatedBets(String normal,Pageable page);

//    Optional<Bet> findByBetCode(int code);

    Page<Bet> findByBetCodeLike(String code, Pageable pageable);

    @Query(value = "SELECT * FROM bets WHERE is_review = 1 AND status = 0 ORDER BY created_at ASC", nativeQuery = true)
    Page<Bet> getRiskyBets(Pageable pageable);

    @Query(value = "SELECT * FROM bets WHERE account = ?1 ORDER BY created_at ASC", nativeQuery = true)
    Page<Bet> getBonusBets(Pageable pageable,String bonus);

    @Query(value = "SELECT * FROM bets WHERE bet_code = ?1 AND status = 0", nativeQuery = true)
    Bet findByBetCode(String code);
    @Query(value = "SELECT user_id FROM bets where created_at BETWEEN ?1 AND ?2", nativeQuery = true)
    List<String> findUserPhones(Timestamp now, Timestamp to);


    @Query(value = "SELECT * FROM bets WHERE account =?1 AND created_at BETWEEN ?2 AND ?3 ORDER BY created_at ASC", nativeQuery = true)
    List<Bet> getBonusBets(String bonus,String from, String to);


    @Query(value = "SELECT * FROM bets WHERE account =?1 AND iso =?2 AND created_at BETWEEN ?3 AND ?4 ORDER BY created_at ASC", nativeQuery = true)
    Page<Bet> getBonusBetsPerCountry(String bonus,String country,Timestamp from, Timestamp to, Pageable pageable);


    @Query(value="SELECT * FROM bets WHERE type = ?1 ORDER BY created_at DESC", nativeQuery = true)
    Page<Bet> getPaginatedJackpotBets(String jackpot, Pageable pageable);


    @Query(value = "SELECT * FROM bets WHERE iso = ?1 AND created_at BETWEEN ?2 AND ?3", nativeQuery = true)
    List<Bet> findBetsWithinAWeek(String country,Timestamp now, Timestamp dt);

    @Query(value = "SELECT * FROM bets WHERE user_id in ?1", nativeQuery = true)
    List<Bet> findByIds(List<Long> customerIds);
}
