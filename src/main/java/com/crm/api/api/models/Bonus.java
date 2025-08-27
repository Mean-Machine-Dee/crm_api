package com.crm.api.api.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Table(name = "bonus")
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Bonus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private long user_id;
    private boolean status;
    @Column(name = "created_at")
    private Timestamp createdAt;
    private String type;
    private String reference;
    private int amount;
    private boolean special;
}
