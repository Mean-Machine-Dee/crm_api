package com.crm.api.api.repository;

import com.crm.api.api.models.OTPS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OTPSRepository extends JpaRepository<OTPS,Long> {
    @Query(value = "SELECT * FROM otps WHERE user_id = ?1 ORDER BY created_at DESC LIMIT 1", nativeQuery = true)
    OTPS findByUser(Long id);
}
