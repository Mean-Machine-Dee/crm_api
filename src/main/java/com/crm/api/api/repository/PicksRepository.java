package com.crm.api.api.repository;

import com.crm.api.api.models.Bet;
import com.crm.api.api.models.Picks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PicksRepository extends JpaRepository<Picks, Long> {
        @Query(value="SELECT * FROM bet_matches WHERE bet_id =?", nativeQuery = true)
        List<Picks> getPicks(Long id);
}
