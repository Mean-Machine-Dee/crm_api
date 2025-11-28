package com.crm.api.crm.repository;

import com.crm.api.crm.models.AgentActivity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AgentActivityRepository extends JpaRepository<AgentActivity, Long> {

    @Query(value = "SELECT * FROM activities WHERE agent_id =?1 ORDER BY created_at DESC", nativeQuery = true)
    Page<AgentActivity> findByAgentId(long id, Pageable pageable);
}
