package com.crm.api.api.models;


import lombok.*;

import javax.persistence.*;

@Table(name = "accounts")
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private double main;
    private double bonus;
    private boolean blocked;
    private boolean block_withdraw;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "owner_id")
    private Customer customer;
}
