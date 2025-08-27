package com.crm.api.payload.requests;


import lombok.*;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class SignUpRequest {
    private String username;
    private String email;
    private String password;
    private Set<String> roles;
    private String iso;
}
