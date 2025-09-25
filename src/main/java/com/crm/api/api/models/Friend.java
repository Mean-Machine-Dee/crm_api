package com.crm.api.api.models;

import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "friends")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Friend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private long invitee;
    private long invite;
    private Timestamp created_at;
    private Timestamp updated_at;
    private boolean redeemed;
}
