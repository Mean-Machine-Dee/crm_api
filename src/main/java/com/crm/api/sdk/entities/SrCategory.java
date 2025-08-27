package com.crm.api.sdk.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Table(name ="sr_categories")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SrCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String country;
    @Column(name = "fr_name")
    private String frName;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sr_sport_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private SrSport sport;

}
