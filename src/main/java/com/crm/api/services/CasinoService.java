package com.crm.api.services;

import com.crm.api.payload.response.GlobalResponse;
import org.springframework.data.domain.Pageable;

public interface CasinoService {
    GlobalResponse casinoBets(String provider, Pageable pageable, String country);

    GlobalResponse getUserCasinos(long id, String provider, Pageable pageable);

    GlobalResponse filterCasinos(String provider, String from, String to, Pageable pageable);
}
