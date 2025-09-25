package com.crm.api.controllers;

import com.crm.api.payload.requests.LonaRequest;
import com.crm.api.payload.response.GlobalResponse;
import com.crm.api.services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @GetMapping("affiliate/program")
    public GlobalResponse affiliates(@RequestParam(value = "from") String from,
                                     @RequestParam(value = "to") String to,
                                     @RequestParam(value = "type" , defaultValue = "landing") String type,
                                     @RequestParam(value = "country" , defaultValue = "BI") String country,
                                      @RequestParam(value = "page",defaultValue = "0") int page,
                                     @RequestParam(value = "size", defaultValue = "15") int size){
        Pageable pageable = PageRequest.of(page,size);
        return
                reportService.getAffiliates(from,to,type,pageable,country);

    }

    @GetMapping("affiliate/program/{id}")
    public GlobalResponse getAffiliate(@PathVariable(value = "id", name = "id") Long id){
        return reportService.getAffiliate(id);
    }




}
