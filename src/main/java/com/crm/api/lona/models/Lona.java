package com.crm.api.lona.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "lona")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Lona {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "bet_code")
    private String code;
    @Column(name = "placed_at")
    private Timestamp placedAt;
    @Column(name = "resulted_at")
    private Timestamp resultedAt;
    private String user;
    private int stake;
    private double payout;
    private String channel;
    @Column(name = "wager_tax")
    private double excise;
    @Column(name = "withHoldingTax")
    private double withHolding;
    private String currency;
    private Double loyalty;
    private boolean status;
    @Column(name = "prsp_dtax")
    private Double prspDeposit;
    @Column(name = "prsp_wtax")
    private Double prspWithdrawal;


}
