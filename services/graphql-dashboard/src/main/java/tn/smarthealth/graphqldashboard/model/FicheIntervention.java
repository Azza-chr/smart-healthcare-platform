package tn.smarthealth.graphqldashboard.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "fiches_intervention")
public class FicheIntervention {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String patientId;
    private String alerteRef;
    private String resume;
    private String statut = "OUVERTE";
    private LocalDateTime creeLe = LocalDateTime.now();

    public Integer getId() { return id; }
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public String getAlerteRef() { return alerteRef; }
    public void setAlerteRef(String alerteRef) { this.alerteRef = alerteRef; }
    public String getResume() { return resume; }
    public void setResume(String resume) { this.resume = resume; }
    public String getStatut() { return statut; }
    public LocalDateTime getCreeLe() { return creeLe; }
}