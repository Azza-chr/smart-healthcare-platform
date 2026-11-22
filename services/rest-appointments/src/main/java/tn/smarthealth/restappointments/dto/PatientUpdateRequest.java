package tn.smarthealth.restappointments.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PatientUpdateRequest(
        @Email @Size(max = 120) String email,
        @Pattern(regexp = "^\\+?[0-9 ]{8,20}$", message = "invalid phone number") String telephone,
        @Size(min = 5, max = 200) String adresse) {
}