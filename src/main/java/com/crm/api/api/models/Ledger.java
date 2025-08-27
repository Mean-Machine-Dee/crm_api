package com.crm.api.api.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ledger")
public class Ledger {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;
    @Column(name="code")
    public String code;
    @Column(name="account")
    public int account;
    @Column(name="reference")
    public String reference;
    @Column(name="credit")
    public double credit;
    @Column(name="debit")
    public double debit;
    @Column(name="balance")
    public double balance;
    @Column(name="uuid")
    public String uuid;
    @Column(name="created_at")
    private Timestamp createdAt;
}
