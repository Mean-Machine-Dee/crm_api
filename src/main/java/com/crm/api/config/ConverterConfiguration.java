package com.crm.api.config;


import com.crm.api.payload.requests.SignUpRequest;
import com.crm.api.payload.requests.SlideRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;


@Configuration
public class ConverterConfiguration implements Converter<String, SlideRequest> {

    @Autowired
    private ObjectMapper objectMapper;


    @Override
    @SneakyThrows
    public SlideRequest convert(String source) {
        return objectMapper.readValue(source,SlideRequest.class);
    }
}
