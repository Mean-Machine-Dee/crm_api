package com.crm.api.api.repository;

import com.crm.api.api.models.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    @Query(value = "SELECT * FROM activities WHERE user_id = ?1 ORDER BY created_at DESC LIMIT 10", nativeQuery = true)
    List<Activity> findByUserId(long id);
}
