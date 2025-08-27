package com.crm.api.services;

import com.crm.api.payload.requests.*;
import com.crm.api.payload.response.GlobalResponse;
import org.springframework.data.domain.Pageable;

public interface FinanceService {
    GlobalResponse search(PaymentsSearchRequest search);

    GlobalResponse filterDepositsByPSP(PSPRequest request);

    GlobalResponse filterPaymentsByPSP(PSPRequest request);

    GlobalResponse filterLona(LonaRequest lonaRequest, Pageable pageable);

    GlobalResponse lonaTaxes(LonaRequest lonaRequest);

    GlobalResponse remit(RemitDate remitDate);

    GlobalResponse remitted(Pageable pageable);

    GlobalResponse setSettings(SettingRequest request);


    GlobalResponse getAppCounter();
}
