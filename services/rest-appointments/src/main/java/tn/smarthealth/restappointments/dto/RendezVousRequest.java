package tn.smarthealth.restappointments.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RendezVousRequest(
        @NotBlank String patientId,
        @NotNull Integer creneauId,
        @NotBlank @Size(max = 200) String motif) {
}