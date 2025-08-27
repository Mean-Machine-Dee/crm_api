package com.crm.api.crm.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Time;
import java.sql.Timestamp;

@Entity
@Table(name = "jackpot_matches")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JackpotGame {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "jackpot_id")
    private Long jackpotId;
    @Column(name = "match_id")
    private long matchId;
    @Column(name = "created_at")
    private Timestamp createdAt;
    @Column(name = "updated_at")
    private Timestamp updatedAt;
    }