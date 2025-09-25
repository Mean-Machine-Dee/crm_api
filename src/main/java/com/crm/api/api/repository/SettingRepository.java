package com.crm.api.api.repository;

import com.crm.api.api.models.Setting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SettingRepository extends JpaRepository<Setting, Long> {

    @Query(value = "SELECT * from global WHERE prsp = ?1", nativeQuery = true)
    List<Setting> findByPRSPName(String service);

    @Query(value = "SELECT * from global ORDER BY country ASC", nativeQuery = true)
    List<Setting> getAll();
}
