package com.crm.api.api.repository;

import com.crm.api.api.models.AppCounter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AppCounterRepository extends JpaRepository<AppCounter, Long> {
  @Query(value = "SELECT * FROM app ORDER BY created_at DESC LIMIT 15", nativeQuery = true)
    List<AppCounter> findTop();
}
