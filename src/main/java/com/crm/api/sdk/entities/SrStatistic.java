package com.crm.api.sdk.entities;


import lombok.*;

import javax.persistence.*;

@Entity
@Table(name ="sr_statistics")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SrStatistic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long sr_competition_id;
    @Column(name = "live_time")
    private String time;
    private  String status;

//    @OneToOne(orphanRemoval = true)
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "sr_competition_id", insertable = false,updatable = false)
    private SrCompetition competition;


}
