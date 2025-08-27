package com.crm.api.sdk.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;

@Entity
@Table(name ="sr_competitions")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SrCompetition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Timestamp scheduled;
    private String status;
    private String name;
    @Column(name = "fr_name")
    private String frName;
    @Column(name = "bet_stop")
    private boolean betStopped;
    @Column(name = "is_highlighted")
    private Integer isHighLight;
    private Integer priority;
    @Column(name = "is_live")
    private boolean isLive;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sr_category_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private SrCategory category;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sr_sport_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private SrSport sport;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "sr_tournament_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private SrTournament tournament;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "sr_competition_id")
    @Where(clause = "sr_market_id in (1,10,18,186,198)")
    @JsonIgnore
    private Set<SrMarket> markets;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "sr_competition_id")
    @JsonIgnore
    private Set<SrMarket> allMarkets;
//
    @OneToOne(mappedBy = "competition")
    private SrStatistic statistics;

    @OneToOne(mappedBy = "competition")
    private SrScore scores;



//    @OneToOne(mappedBy = "score")
//    private SrScore score;
}
