package com.crm.api.crm.models;

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
public class AgentActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String description;
    private String system;
    private String type;
    private String phone;
    @Column(name = "created_at")
    private Timestamp createdAt;
}
