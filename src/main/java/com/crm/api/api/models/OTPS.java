package com.crm.api.api.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Table(name = "otps")
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OTPS {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
    private long userId;
    private String phone;
    private int otp;
    private boolean expired;
}
