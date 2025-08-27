package com.crm.api.api.models;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "deposits")
@Entity
public class Deposit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String tx_id;
    private String telco;
    @Column(name = "reference_code")
    private String refCode;
    private double amount;
    private String status;
    private String name;
    private String currency;
    private double fees;
    private String phone;
    private long user_id;
    private long agent_source;
    private String message;
    @Column(name = "made_by")
    private String madeBy;
    private Timestamp date_deposited;

}
