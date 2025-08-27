package com.crm.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class JackpotGameDTO {
    private long id;
    private String name;
    private Date scheduled;
    private String sport;
    private String country;
    private List<Double> odds;
}
