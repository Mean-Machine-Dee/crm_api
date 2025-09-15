package com.crm.api.services;


import com.crm.api.payload.requests.DispatchRequest;
import com.crm.api.payload.response.GlobalResponse;
import org.springframework.data.domain.Pageable;

import java.security.Principal;


public interface DashboardService {
    GlobalResponse getTodaysAggregates(String country, String from, String to, String stage);
    GlobalResponse getAggregates(String country, String from, String to, String stage);

    GlobalResponse dispatchMessage(DispatchRequest dispatchRequest, Principal principal);

    GlobalResponse sendNotification(long id);

    GlobalResponse getDispatches(Pageable pageable);

    GlobalResponse getBonusAbusers(String from, String to);

    GlobalResponse getSignUpsByIso(String country, String from, String to, String stage);

    GlobalResponse getSignUps();

    GlobalResponse getSignUpsByCountry(String country);
}