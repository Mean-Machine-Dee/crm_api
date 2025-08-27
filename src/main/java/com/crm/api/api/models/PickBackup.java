package com.crm.api.api.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;


@Table(name = "pick_bcks")
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PickBackup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long sr_competition_id;
    private Long sr_market_id;
    private Long sr_outcome_id;
    private double odds;
    private String name;
    private String pick;
    private boolean status;
    private boolean resulted;
    private boolean won;
    private Timestamp deleted_at;
    private String specifier;
}