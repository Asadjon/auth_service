package org.cyber_pantera.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResetPasswordRequest {

    @NotBlank(message = "Token must not be empty")
    private String token;

    @NotBlank(message = "New password must not be empty")
    private String newPassword;

    @NotBlank(message = "Confirmation password must not be empty")
    private String confirmPassword;
}
