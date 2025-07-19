package org.cyber_pantera.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.cyber_pantera.entity.Role;

@Data
@AllArgsConstructor
public class UserResponse {
    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
}
