package com.crm.api.payload.requests;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CampaignRequest {
    private String description;
    private String type;
    private String cta;
    private String lang;
    private String actionDay;
    private String header;
    private String ctaLink;
    private String status;
    private String subHeader;
    private String expiryDay;
}
