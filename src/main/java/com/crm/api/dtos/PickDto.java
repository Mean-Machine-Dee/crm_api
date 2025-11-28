package com.crm.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PickDto {
    private Long sr_competition_id;
    private Long sr_market_id;
    private String market_name;
    private Long sr_outcome_id;
    private double odds;
    private String name;
    private String pick;
    private boolean status;
    private boolean resulted;
    private boolean won;
    private Timestamp deleted_at;
    private String specifier;
    private String pick_name;
    private String sport;
    private boolean voided;
    private String score;
}
