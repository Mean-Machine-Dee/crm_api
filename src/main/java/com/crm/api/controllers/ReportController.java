package com.crm.api.controllers;

import com.crm.api.payload.requests.LonaRequest;
import com.crm.api.payload.response.GlobalResponse;
import com.crm.api.services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/reports/")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @PostMapping("dashboard")
    public GlobalResponse dashboard(@RequestBody LonaRequest lonaRequest){
        return reportService.dashboard(lonaRequest);
    }

    @PostMapping("providers")
    public GlobalResponse providers(@RequestBody LonaRequest lonaRequest){
        return reportService.providerReport(lonaRequest);
    }




}
