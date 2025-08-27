package com.crm.api.crm.repository;

import com.crm.api.crm.models.JackpotGame;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JackpotMatchRepository extends JpaRepository<JackpotGame,Long> {
}
