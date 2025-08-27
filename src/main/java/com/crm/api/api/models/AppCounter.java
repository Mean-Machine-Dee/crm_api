package com.crm.api.api.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Table(name = "app")
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AppCounter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "created_at")
    private Timestamp createdAt;
    @Column(name = "device_id")
    private String deviceId;

}
