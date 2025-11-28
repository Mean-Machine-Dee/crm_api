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
    private Long id;
    private String description;
    private String phone;
    @Column(name = "created_at")
    private Timestamp createdAt;
    @Column(name = "agent_id")
    private long agentId;
}
