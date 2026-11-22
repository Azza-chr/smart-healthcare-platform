package tn.smarthealth.restappointments.dto;

import java.time.LocalDate;

public record OrdonnanceDto(
        Integer id,
        String medecinNom,
        LocalDate dateOrdonnance,
        String medicament,
        String posologie,
        Integer dureeJours) {
}