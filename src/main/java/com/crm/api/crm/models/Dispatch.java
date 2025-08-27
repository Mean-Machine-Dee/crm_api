package com.crm.api.crm.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "dispatches")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Dispatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "agent_id")
    private long agentId;
    private String description;
    private boolean status;
    @Column(name = "created_at")
    private Timestamp createdAt;
    @Column(name = "updated_at")
    private Timestamp updatedAt;
    @Column(name = "dispatch_date")
    private Timestamp dispatchDate;
    private String type;
    private String lang;
}
