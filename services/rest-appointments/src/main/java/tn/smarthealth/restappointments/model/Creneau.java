package tn.smarthealth.restappointments.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "creneaux")
public class Creneau {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medecin_id")
    private Medecin medecin;

    private LocalDateTime debut;
    private LocalDateTime fin;
    private boolean urgence;
    private String statut = "LIBRE";      // LIBRE or OCCUPE

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Medecin getMedecin() { return medecin; }
    public void setMedecin(Medecin medecin) { this.medecin = medecin; }

    public LocalDateTime getDebut() { return debut; }
    public void setDebut(LocalDateTime debut) { this.debut = debut; }

    public LocalDateTime getFin() { return fin; }
    public void setFin(LocalDateTime fin) { this.fin = fin; }

    public boolean isUrgence() { return urgence; }
    public void setUrgence(boolean urgence) { this.urgence = urgence; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
}