package com.crm.api.crm.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "slides")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Slide {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "slides_id")
    private String slideId;
    private String name;
    @Column(name = "created_at")
    private Timestamp createdAt;
    @Column(name = "updated_at")
    private Timestamp updatedAt;
    private boolean active;
    private String language;
    private String type;
    private String iso;
}
