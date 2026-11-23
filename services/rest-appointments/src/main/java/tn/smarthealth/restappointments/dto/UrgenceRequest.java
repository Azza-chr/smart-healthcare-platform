package tn.smarthealth.restappointments.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UrgenceRequest(
        @NotBlank String patientId,
        String specialite,
        @NotBlank @Size(max = 200) String motif) {
}