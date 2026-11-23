package tn.smarthealth.graphqldashboard.dto;

public record FicheView(Integer id, String patientId, String alerteRef, String resume,
                        String statut, String creeLe) {
}