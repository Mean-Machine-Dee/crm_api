package com.crm.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CustomerDTO {
    private String phone;
    private String country;
    private String joined;
    private double main;
    private double bonus;
    private long id;
    private boolean verified;
    private boolean blocked;
    private boolean canWithdraw;
}
