package tn.smarthealth.graphqldashboard.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "soins")
public class Soin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String patientId;
    private LocalDate dateSoin;
    private String type;
    private String description;
    private String praticien;

    public Integer getId() { return id; }
    public String getPatientId() { return patientId; }
    public LocalDate getDateSoin() { return dateSoin; }
    public String getType() { return type; }
    public String getDescription() { return description; }
    public String getPraticien() { return praticien; }
}