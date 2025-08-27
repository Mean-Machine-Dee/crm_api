package com.crm.api.payload.response;

import com.crm.api.dtos.CasinoDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CasinoResponse {
    private List<CasinoDTO> data;
    private int page;
    private int totalPages;
    private int nextPage;
    private long totalItems;
}
