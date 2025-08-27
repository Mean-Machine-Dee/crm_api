package com.crm.api.config;


import com.crm.api.payload.requests.CampaignRequest;
import com.crm.api.payload.requests.SignUpRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
@Configuration
public class CampaignConverter implements Converter<String, CampaignRequest> {
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    @SneakyThrows
    public CampaignRequest convert(String source) {
        return objectMapper.readValue(source,CampaignRequest.class);
    }
}
