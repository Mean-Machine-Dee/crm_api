package com.crm.api.sdk.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.List;


@Entity
@Table(name ="sr_competition_markets")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SrMarket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "competition_market_id")
    private String id;

    @Column(name = "sr_market_id")
    private long marketId;

    private String name;
    private int status;
    @Column(name = "fr_name")
    private String frName;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sr_competition_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private SrCompetition competition;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "market_competition_id")
    private List<SrOutcomes> outcomes;
}
