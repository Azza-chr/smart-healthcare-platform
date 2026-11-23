package tn.smarthealth.authservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = "username is required") @Size(max = 40) String username,
        @NotBlank(message = "password is required") @Size(max = 100) String password) {
}