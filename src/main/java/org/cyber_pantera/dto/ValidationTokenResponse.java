package org.cyber_pantera.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.cyber_pantera.entity.Role;

@Data
@AllArgsConstructor
public class ValidationTokenResponse {
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
}
