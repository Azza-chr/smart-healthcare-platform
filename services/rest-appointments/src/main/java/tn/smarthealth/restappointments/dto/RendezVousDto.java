package tn.smarthealth.restappointments.dto;

import java.time.LocalDateTime;

public record RendezVousDto(
        Integer id,
        String patientId,
        String patientNom,
        CreneauDto creneau,
        String motif,
        String type,
        String statut,
        LocalDateTime creeLe) {
}