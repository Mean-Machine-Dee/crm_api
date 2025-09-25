package com.crm.api.payload.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LonaRequest {
    private String from;
    private String to;
    private String country;
}
