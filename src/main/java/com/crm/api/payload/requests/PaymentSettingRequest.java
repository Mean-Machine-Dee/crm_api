package com.crm.api.payload.requests;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaymentSettingRequest {
    private String service;
    private String status;
    private String prsp;
    private LocalDate activeAt;
}
