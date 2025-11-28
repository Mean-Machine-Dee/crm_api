package com.crm.api.crm.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "campaigns")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Campaign {
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
    private LocalDateTime dispatchDate;
    private String type;
    private String cta;
    private String lang;
    private String thumbnail;
    @Column(name = "cta_link")
    private String ctaLink;
    @Column(name = "header")
    private String header;
    @Column(name = "sub_header")
    private String subHeader;
    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;
}