package tn.smarthealth.graphqldashboard.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "dossiers")
public class Dossier {

    @Id
    private String patientId;
    private String nom;
    private String prenom;
    private LocalDate dateNaissance;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "service_id")
    private ServiceHospitalier service;

    private String pathologie;
    private String niveauRisque;
    private String medecinReferent;

    public String getPatientId() { return patientId; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public LocalDate getDateNaissance() { return dateNaissance; }
    public ServiceHospitalier getService() { return service; }
    public String getPathologie() { return pathologie; }
    public String getNiveauRisque() { return niveauRisque; }
    public String getMedecinReferent() { return medecinReferent; }
}