package org.cyber_pantera.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForgotPasswordRequest {

    @NotBlank(message = "Email must not be empty")
    @Email(message = "Invalid email format")
    private String email;
}
