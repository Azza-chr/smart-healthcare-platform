package tn.smarthealth.graphqldashboard.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "traitements")
public class Traitement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String patientId;
    private String medicament;
    private String posologie;
    private LocalDate debut;
    private LocalDate fin;      // null = ongoing treatment

    public Integer getId() { return id; }
    public String getPatientId() { return patientId; }
    public String getMedicament() { return medicament; }
    public String getPosologie() { return posologie; }
    public LocalDate getDebut() { return debut; }
    public LocalDate getFin() { return fin; }
}