package org.cyber_pantera.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.cyber_pantera.entity.Role;

@Data
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String accessToken;
    private String email;
    private Role role;
}
