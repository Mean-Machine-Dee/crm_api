package com.crm.api.payload.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SlideRequest {
    private String lang;
    private List<String> category;
    private List<String> iso;
    private String description;
    private String type;
    private String cta;
    private String status;

}
