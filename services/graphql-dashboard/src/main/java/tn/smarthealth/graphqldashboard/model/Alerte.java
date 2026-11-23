package tn.smarthealth.graphqldashboard.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alertes")
public class Alerte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String patientId;
    private String type;
    private String niveau;
    private String message;
    private LocalDateTime creeLe;

    public Integer getId() { return id; }
    public String getPatientId() { return patientId; }
    public String getType() { return type; }
    public String getNiveau() { return niveau; }
    public String getMessage() { return message; }
    public LocalDateTime getCreeLe() { return creeLe; }
}