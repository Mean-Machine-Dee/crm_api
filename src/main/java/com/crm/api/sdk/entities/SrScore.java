package com.crm.api.sdk.entities;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name ="sr_scores")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SrScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sr_competition_id;
    private String scores;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "sr_competition_id", insertable = false,updatable = false)
    private SrCompetition competition;

}