package com.crm.api.api.models;


import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;

@Table(name = "users")
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String phone;
    private boolean verified;
    @Column(name = "created_at")
    private Timestamp createdAt;
    private String iso;

    @OneToOne(mappedBy = "customer", fetch = FetchType.LAZY)
    private Account account;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private Set<Bet> bets;

}
