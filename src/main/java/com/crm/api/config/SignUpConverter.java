package com.crm.api.config;
import org.springframework.core.convert.converter.Converter;
import com.crm.api.payload.requests.SignUpRequest;
import com.crm.api.payload.requests.SlideRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SignUpConverter implements Converter<String, SignUpRequest> {
    @Autowired
    private ObjectMapper objectMapper;


    @Override
    @SneakyThrows
    public SignUpRequest convert(String source) {
        return objectMapper.readValue(source,SignUpRequest.class);
    }
}
