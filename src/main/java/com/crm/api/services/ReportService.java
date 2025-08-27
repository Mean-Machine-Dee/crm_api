package com.crm.api.services;

import com.crm.api.payload.requests.LonaRequest;
import com.crm.api.payload.response.GlobalResponse;

public interface ReportService {
    GlobalResponse dashboard(LonaRequest lonaRequest);

    GlobalResponse providerReport(LonaRequest lonaRequest);
}
