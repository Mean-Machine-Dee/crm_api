package com.crm.api.payload.requests;

import com.crm.api.dtos.FeaturedDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TournamentRequest {
    private List<FeaturedDTO> ids;
    private String type;
}
