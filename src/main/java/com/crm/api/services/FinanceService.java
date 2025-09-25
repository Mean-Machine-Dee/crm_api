package com.crm.api.services;

import com.crm.api.payload.requests.*;
import com.crm.api.payload.response.GlobalResponse;
import org.springframework.data.domain.Pageable;

public interface FinanceService {
    GlobalResponse search(PaymentsSearchRequest search);

    GlobalResponse filterDepositsByPSP(PSPRequest request);

    GlobalResponse filterPaymentsByPSP(PSPRequest request);

    GlobalResponse filterLona(LonaRequest lonaRequest, Pageable pageable);

    GlobalResponse lonaTaxes(LonaRequest lonaRequest, String type);

    GlobalResponse remit(RemitDate remitDate);

    GlobalResponse remitted(Pageable pageable);

    GlobalResponse setSettings(SettingRequest request);


    GlobalResponse getAppCounter();

    GlobalResponse payDirect(DirectTransferRequest request);

    GlobalResponse filterCashflow(String type);

    GlobalResponse getPaymentSettings();

    GlobalResponse filterTodays(String prsp, String currency);

    GlobalResponse filterPaymentsToDay(String prsp, String currency);
}
