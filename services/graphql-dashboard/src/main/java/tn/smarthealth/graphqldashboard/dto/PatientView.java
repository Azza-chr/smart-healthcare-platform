package tn.smarthealth.graphqldashboard.dto;

public record PatientView(String id, String nom, String prenom, String dateNaissance, String service,
                          String pathologie, String niveauRisque, String medecinReferent) {
}