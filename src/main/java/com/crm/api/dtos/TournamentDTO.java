package com.crm.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TournamentDTO {
    private long id;
    private String country;
    private String tournament;
    private String sport;
    private Integer featured;
}
