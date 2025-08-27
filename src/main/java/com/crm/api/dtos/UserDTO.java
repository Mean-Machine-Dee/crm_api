package com.crm.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserDTO {
    private String email;
    private long userId;
    private String username;
    private String thumbnail;
    private Set<String> roles;
    private String iso;

}
