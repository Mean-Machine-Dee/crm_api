package com.crm.api.crm.repository;

import com.crm.api.crm.models.Campaign;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    @Query(value = "SELECT * FROM campaigns", nativeQuery = true)
    Page<Campaign> findCampaigns(Pageable pageable);
}
