package tn.smarthealth.restappointments.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RendezVousUpdateRequest(
        @NotNull Integer creneauId,
        @Size(max = 200) String motif) {
}