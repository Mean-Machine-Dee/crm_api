package com.crm.api.payload.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SettingRequest {
    private String service;
    private String status;
    private int span;
    private String prsp;
}
