package com.crm.api.controllers;

import com.crm.api.payload.requests.*;
import com.crm.api.payload.response.GlobalResponse;
import com.crm.api.services.FinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/finance/")
public class FinanceController{

    @Autowired
    private FinanceService financeService;

    @PostMapping("payments")
    public GlobalResponse search(@RequestBody PaymentsSearchRequest search){
        return financeService.search(search);
    }

    @PostMapping("filter/deposits")
    public GlobalResponse filter(@RequestBody PSPRequest request){
        return financeService.filterDepositsByPSP(request);
    }

    @GetMapping("todays/cashflow")
    public GlobalResponse filterToday(@RequestParam(name = "type") String type){
        return financeService.filterCashflow(type);
    }

    @PostMapping("filter/payments")
    public GlobalResponse filterPayments(@RequestBody PSPRequest request){
        return financeService.filterPaymentsByPSP(request);
    }

    @PostMapping("lona/bets")
    public GlobalResponse getBets(@RequestBody LonaRequest lonaRequest,@RequestParam(value = "page",defaultValue = "0") int page,
                                  @RequestParam(value = "size", defaultValue = "15") int size
    ){
        Pageable pageable = PageRequest.of(page,size);
        return financeService.filterLona(lonaRequest,pageable);
    }

    @PostMapping("lona/taxes")
    public GlobalResponse getTaxes(@RequestBody LonaRequest lonaRequest){
        return financeService.lonaTaxes(lonaRequest);
    }

    @PostMapping("pay/bills")
    public GlobalResponse getTaxes(@RequestBody   DirectTransferRequest request){
        return financeService.payDirect(request);
    }



    @PostMapping("lona/remit")
    public GlobalResponse remit(@RequestBody RemitDate remitDate){
        return financeService.remit(remitDate);
    }

    @GetMapping("lona/submitted")
    public GlobalResponse remmited(@RequestParam(value = "page", defaultValue = "0") int page,
                                   @RequestParam(value = "size", defaultValue = "15") int size){
        Pageable pageable = PageRequest.of(page,size);
        return financeService.remitted(pageable);
    }

    @PostMapping("settings")
    public GlobalResponse setting(@RequestBody SettingRequest request, Principal principal){
        return financeService.setSettings(request,principal);
    }


    @GetMapping("payments/settings")
    public GlobalResponse setting(){
        return financeService.getPaymentSettings();
    }
    @GetMapping("app/counter")
    public GlobalResponse counter(){
        return financeService.getAppCounter();
    }

    @GetMapping("filter/todays/deposit")
    public GlobalResponse filterDeposits(@RequestParam(value = "prsp", defaultValue = "na") String prsp,
                                       @RequestParam(value = "currency", defaultValue = "na") String currency){
        return financeService.filterTodays(prsp,currency);
    }



    @GetMapping("filter/todays/payments")
    public GlobalResponse filterPayments(@RequestParam(value = "prsp", defaultValue = "na") String prsp,
                                       @RequestParam(value = "currency", defaultValue = "na") String currency){
        return financeService.filterPaymentsToDay(prsp,currency);
    }


}
