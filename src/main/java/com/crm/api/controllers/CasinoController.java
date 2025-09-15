package com.crm.api.controllers;


import com.crm.api.payload.response.GlobalResponse;
import com.crm.api.services.CasinoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(path = "/api/casino/")
public class CasinoController {

    @Autowired
    private CasinoService service;

    @GetMapping("bets")
    public GlobalResponse getCasinoBets(@RequestParam(name = "provider", defaultValue = "pragmatic") String provider,
                                        @RequestParam(name = "country", defaultValue = "BI") String country,
                                        @RequestParam(name = "page", defaultValue = "0") int page,
                                        @RequestParam(name = "size", defaultValue = "15") int size
    ) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("created_at").descending());
        return service.casinoBets(provider, pageable,country);
    }


    @GetMapping("user/{id}")
    public GlobalResponse getByUserId(@PathVariable(name = "id") long id, @RequestParam(name = "provider", defaultValue = "pragmatic") String provider,
                                      @RequestParam(name = "page", defaultValue = "0") int page,
                                      @RequestParam(name = "size", defaultValue = "15") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("created_at").descending());
        return service.getUserCasinos(id, provider, pageable);
    }


    @GetMapping("filter")
    public GlobalResponse filterByDate(@RequestParam(name = "provider", defaultValue = "pragmatic") String provider,
                                       @RequestParam(name = "from", defaultValue = "from") String from,
                                       @RequestParam(name = "to", defaultValue = "to") String to,
                                       @RequestParam(name = "page", defaultValue = "0") int page,
                                       @RequestParam(name = "size", defaultValue = "15") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("created_at").descending());
        return service.filterCasinos(provider, from, to, pageable);

    }
}
