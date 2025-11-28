package com.crm.api.dtos;

import java.sql.Timestamp;

public interface AffiliateSerializer {
    Long getId();
    String getPhone();
    String getIso();
    Timestamp getCreated_at();
}
