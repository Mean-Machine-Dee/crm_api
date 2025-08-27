package com.crm.api.payload.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TicketRequest {
    @Size(min = 10)
    private String name;
    @Size(min = 20)
    @NotBlank(message = "Name is mandatory field")
    private String description;
    @Size(min = 10)
    private String phone;
    @Size(min = 10)
    private String issueType;
    private String status;

}
