package com.crm.api.crm.repository;

import com.crm.api.crm.models.Dispatch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DispatchRepository extends JpaRepository<Dispatch, Long> {
    @Query(value = "SELECT * FROM dispatches", nativeQuery = true)
    Page<Dispatch> findAllDispatches(Pageable pageable);
}
