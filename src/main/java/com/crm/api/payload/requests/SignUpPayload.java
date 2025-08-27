package com.crm.api.payload.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public class SignUpPayload {
        private String actionDay;
        private String lang;
        private List<String> category;
        private String description;
        private String type;
        private String cta;
        private String status;

    }

