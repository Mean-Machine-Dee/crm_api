package com.crm.api.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GlobalResponse {
    private Object data;
    private boolean success;
    private boolean error;
    private String message;
}
