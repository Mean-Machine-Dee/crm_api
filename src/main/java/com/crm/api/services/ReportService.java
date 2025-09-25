package com.crm.api.services;

import com.crm.api.payload.requests.LonaRequest;
import com.crm.api.payload.response.GlobalResponse;
import org.springframework.data.domain.Pageable;

public interface ReportService {
    GlobalResponse dashboard(LonaRequest lonaRequest);

    GlobalResponse providerReport(LonaRequest lonaRequest);

    GlobalResponse getAffiliates(String from, String to, String type, Pageable pageable, String country);

    GlobalResponse getAffiliate(Long id);
}
