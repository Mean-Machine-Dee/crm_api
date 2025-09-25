package com.crm.api.dtos;

import javax.persistence.Column;
import java.time.LocalDateTime;

public interface PlagmaticInterface {
    long getId();
     long getUserId();
      String getGameId();
      Double getAmount();
     String getStatus();
       String getWon();
      LocalDateTime getResultedAt();
     String getRoundDetails();
     Double getAmountWon();
     LocalDateTime getCreatedAt();
     String getIso();
}
