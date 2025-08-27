package com.crm.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class EditDTO {
    private String email;
    private String username;
    private Set<String> roles;
    private String password;
}
