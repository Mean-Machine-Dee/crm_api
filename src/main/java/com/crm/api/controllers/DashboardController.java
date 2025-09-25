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
    public GlobalResponse aggregation(@RequestParam(name = "country") String country,
                                      @RequestParam(name = "from") String from,
                                      @RequestParam(name = "to") String to,
                                      @RequestParam(name = "stage",defaultValue = "landing") String stage){
        return dashboardService.getAggregates(country,from,to,stage);
    }

    @GetMapping("todays")
    public GlobalResponse todayAggregates(@RequestParam(name = "country") String country,
                                          @RequestParam(name = "from") String from,
                                          @RequestParam(name = "to") String to,
                                          @RequestParam(name = "stage",defaultValue = "landing") String stage){
        return dashboardService.getTodaysAggregates(country,from,to,stage);
    }

    @GetMapping("signups")
    public GlobalResponse signups(@RequestParam(name = "country") String country,@RequestParam(name = "from") String from,
                                  @RequestParam(name = "to") String to,
                                  @RequestParam(name = "stage",defaultValue = "landing") String stage ){
        return dashboardService.getSignUpsByIso(country,from,to,stage);
    }

    @GetMapping("signups/count")
    public GlobalResponse signupsCount(@RequestParam(name = "country") String country,
                                       @RequestParam(name = "from") String from,
                                       @RequestParam(name = "to") String to){
        return dashboardService.getSignUps(country,from,to);
    }

    @GetMapping("signups/country")
    public GlobalResponse signupsCount(@RequestParam(name = "country") String country){
        return dashboardService.getSignUpsByCountry(country);
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
    public GlobalResponse bonusAbusers(@RequestParam(name = "from") String from, @RequestParam(name = "to") String to,
                                       @RequestParam(name = "country") String country){
        return dashboardService.getBonusAbusers(from,to,country);
    }




}
