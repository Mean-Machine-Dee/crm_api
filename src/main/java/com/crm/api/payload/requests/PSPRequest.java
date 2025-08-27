package com.crm.api.payload.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PSPRequest {
    private String name;
    private String currency;
    private String from;
    private String to;
}
