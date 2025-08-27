package com.crm.api.api.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table(name = "aviatrix_bets")
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Aviatrix {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "user_id")
    private long userId;
    @Column(name = "product_id")
    private String gameId;
    private double amount;
    private double odds;
    private boolean status;
    private String currency;
    private String won;
    @Column(name = "deleted_at")
    private LocalDateTime resultedAt;
    @Column(name = "amount_won")
    private Double amountWon;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
