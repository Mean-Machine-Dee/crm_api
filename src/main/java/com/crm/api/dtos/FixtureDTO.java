package com.crm.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class FixtureDTO {
    private long id;
    private String name;
    private Date scheduled;
    private String sport;
    private String country;
}
