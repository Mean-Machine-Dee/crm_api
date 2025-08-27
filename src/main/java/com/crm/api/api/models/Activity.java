package com.crm.api.api.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "activities")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String activity;
    private String system;
    private String type;
    @Column(name = "created_at")
    private Timestamp createdAt;
}
