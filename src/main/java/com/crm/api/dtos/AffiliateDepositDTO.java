package com.crm.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AffiliateDepositDTO {
    private String phone;
    private long userId;
    private int amount;
    private int commision;
    private Timestamp depositedAt;
    private long host;
    private boolean hasDeposit;
}
