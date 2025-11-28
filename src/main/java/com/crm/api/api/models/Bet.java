package com.crm.api.api.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;
@Entity
@Table(name = "bets")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Bet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int amount;
    private double factor;
    private double payout;
    private boolean multibet;
    private boolean status;
    private boolean won;
    private String platform;
    @Column(name = "bet_code")
    private String betCode;
    private String account;
    private Timestamp bet_placed;
    private Timestamp cancelled_date;
    private Timestamp deleted_at;
    @Column(name = "is_review")
    private boolean isReview;
    private boolean clean;
    @Column(name = "user_id")
    private long userId;
    private String iso;


//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "user_id")
//    private Customer customer;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//    @JsonIgnore
    @JoinColumn(name = "bet_id")
    private Set<Picks> picks;
}
