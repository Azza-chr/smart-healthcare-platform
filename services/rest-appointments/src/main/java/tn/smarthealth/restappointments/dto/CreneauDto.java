package tn.smarthealth.restappointments.dto;

import java.time.LocalDateTime;

public record CreneauDto(
        Integer id,
        Integer medecinId,
        String medecinNom,
        String specialite,
        LocalDateTime debut,
        LocalDateTime fin,
        boolean urgence,
        String statut) {
}