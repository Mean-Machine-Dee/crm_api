package com.crm.api.api.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table(name = "plagmatic_bets")
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JetX {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "user_id")
    private long userId;
    @Column(name = "game_name")
    private String gameName;
    private double amount;
    @Column(name = "amount_won")
    private Double amountWon;
    private String status;
    @Column(name = "currency_code")
    private String currency;
    private String won;
    @Column(name = "deleted_at")
    private LocalDateTime resultedAt;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "roll_back")
    private LocalDateTime rolledBack;
}
