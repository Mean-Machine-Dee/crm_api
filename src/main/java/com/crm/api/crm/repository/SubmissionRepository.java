package com.crm.api.crm.repository;

import com.crm.api.crm.models.Submission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    @Query(value = "SELECT * FROM submissions ORDER BY created_at DESC", nativeQuery = true)
    Page<Submission> findAllData(Pageable pageable);
}
