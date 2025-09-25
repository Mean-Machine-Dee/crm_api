package com.crm.api.services;

import com.crm.api.payload.response.GlobalResponse;
import org.springframework.data.domain.Pageable;

public interface CasinoService {
    GlobalResponse casinoBets(String provider, Pageable pageable, String country, String from, String to, String type);

    GlobalResponse getUserCasinos(long id, String provider, Pageable pageable);


}
