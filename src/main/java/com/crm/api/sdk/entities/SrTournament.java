package com.crm.api.sdk.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name ="sr_tournaments")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SrTournament {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Integer featured;
    private Integer priority;
    @Column(name = "fr_name")
    private String frName;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "sr_tournament_id")
    private Set<SrCompetition> competitions;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "sr_category_id", nullable = false)
    private SrCategory category;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "sr_sport_id", nullable = false)
    private SrSport sport;

}
