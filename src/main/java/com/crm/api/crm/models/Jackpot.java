package com.crm.api.crm.models;

import com.crm.api.sdk.entities.SrCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "jackpots")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Jackpot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "jackpot_code")
    private String jackpotCode;
    @Column(name = "created_at")
    private Timestamp createdAt;
    @Column(name = "updated_at")
    private Timestamp updatedAt;
    private Timestamp starts;
    private Timestamp completes;
    private boolean status;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "jackpot_id")
    private List<JackpotGame> games;


}