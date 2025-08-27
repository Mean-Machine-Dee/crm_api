package com.crm.api.api.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Table(name = "bet_bcks")
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BetBackup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private long user_id;
    private int amount;
    private double factor;
    private double payout;
    private boolean multibet;
    private boolean status;
    private boolean won;
    private String platform;
    private String bet_code;
    private String account;
    private Timestamp bet_placed;
    private Timestamp deleted_at;

}
