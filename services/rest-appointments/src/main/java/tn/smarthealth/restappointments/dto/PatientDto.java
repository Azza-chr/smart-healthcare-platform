package tn.smarthealth.restappointments.dto;

import java.time.LocalDate;

public record PatientDto(
        String id,
        String nom,
        String prenom,
        LocalDate dateNaissance,
        String email,
        String telephone,
        String adresse) {
}