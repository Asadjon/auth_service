package org.cyber_pantera.dto;

import lombok.Data;
import org.cyber_pantera.entity.Role;

@Data
public class RegisterRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Role role;
}
