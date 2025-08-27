package com.crm.api.services;


import com.crm.api.payload.requests.DispatchRequest;
import com.crm.api.payload.response.GlobalResponse;
import org.springframework.data.domain.Pageable;

import java.security.Principal;


public interface DashboardService {
    GlobalResponse getTodaysAggregates();
    GlobalResponse getAggregates();

    GlobalResponse dispatchMessage(DispatchRequest dispatchRequest, Principal principal);

    GlobalResponse sendNotification(long id);

    GlobalResponse getDispatches(Pageable pageable);

    GlobalResponse getBonusAbusers(String from, String to);
}