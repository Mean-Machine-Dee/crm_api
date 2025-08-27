package com.crm.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CasinoDTO {

    private String origin;
    private String gameName;
    private double amount;
    private Double amountWon;
    private String status;
    private String won;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;
    private double odds;
    private String currency;
    private long userId;
}
