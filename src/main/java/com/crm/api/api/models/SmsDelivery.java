package com.crm.api.api.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Table(name = "s_m_s_deliverlies")
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor

public class SmsDelivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String msisdn;
    private String description;
    private String origin;
    private Timestamp created_at;
}
