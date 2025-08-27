package com.crm.api.sdk.repositories;

import com.crm.api.sdk.entities.SrCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SrCategoryRepository extends JpaRepository<SrCategory, Long> {
    List<SrCategory> findByCountryLike(String country);
}
