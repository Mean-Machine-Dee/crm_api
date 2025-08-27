package com.crm.api.payload.requests;


import com.crm.api.dtos.FeaturedDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JackpotRequest {
    @Size(min = 17, max = 17)
    List<FeaturedDTO>  gameIds;
    String starts;
    String completes;
}
