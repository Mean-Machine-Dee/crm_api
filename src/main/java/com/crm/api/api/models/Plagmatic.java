package com.crm.api.api.models;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(name = "plagmatic_bets")
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Plagmatic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "user_id")
    private long userId;
    @Column(name = "game_id")
    private String gameId;
    private double amount;
    private String status;
    private String won;
    @Column(name = "deleted_at")
    private LocalDateTime resultedAt;
    @Column(name = "round_details")
    private String roundDetails;
    @Column(name = "amount_won")
    private Double amountWon;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
