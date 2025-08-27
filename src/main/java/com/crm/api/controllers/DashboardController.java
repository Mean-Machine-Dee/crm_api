package com.crm.api.controllers;


import com.crm.api.crm.models.User;
import com.crm.api.dtos.UserDTO;
import com.crm.api.payload.requests.DispatchRequest;
import com.crm.api.payload.response.GlobalResponse;
import com.crm.api.services.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/crm/dashboard/")
public class DashboardController {

    @Autowired
   DashboardService dashboardService;


    @GetMapping("aggregation")
    public GlobalResponse aggregation(){
        return dashboardService.getAggregates();
    }

    @GetMapping("todays")
    public GlobalResponse todayAggregates(){
        return dashboardService.getTodaysAggregates();
    }

    @PostMapping("dispatches")
    public GlobalResponse dispatch(@RequestBody DispatchRequest dispatchRequest, Principal principal){
        return dashboardService.dispatchMessage(dispatchRequest, principal);
    }


    @GetMapping("dispatches")
    public GlobalResponse getDispatch(@RequestParam(name = "page") int page, @RequestParam(name = "size") int size){
        Pageable pageable = PageRequest.of(page,size, Sort.by("created_at").descending());
        return dashboardService.getDispatches(pageable);
    }

    @GetMapping("dispatch/message/{id}")
    public GlobalResponse dispatchMessage(@PathVariable(name = "id") long id){
        return dashboardService.sendNotification(id);

    }

    @GetMapping("bonus/abusers")
    public GlobalResponse bonusAbusers(@RequestParam(name = "from") String from, @RequestParam(name = "to") String to){
        return dashboardService.getBonusAbusers(from,to);
    }




}
