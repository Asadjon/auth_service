package org.cyber_pantera.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.cyber_pantera.entity.Role;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String email;
    private Role role;
}
