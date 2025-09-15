package com.crm.api.payload.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DirectTransferRequest {
    private String phone;
    private String provider = "lumicash";
    private int amount;
    private String currency = "BIF";
    private String country = "BI";
}
