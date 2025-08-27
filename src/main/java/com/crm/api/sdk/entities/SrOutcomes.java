package com.crm.api.sdk.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Table(name ="sr_outcomes_odds")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SrOutcomes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    @Column(name = "sr_outcome_id")
    private String outcomeId;
    private String name;
    @Column(name = "fr_name")
    private String frName;
    @Column(name = "market_competition_id")
    private String competitionMarketId;
    @Column(name = "sr_market_id")
    private long marketId;
    @Column(name = "sr_competition_id")
    private long competitionId;
    private int status;
    private String specifier;
    private double odds;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "market_competition_id", referencedColumnName = "competition_market_id", nullable = false, insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private SrMarket market;
}
