package tn.smarthealth.graphqldashboard.model;

import jakarta.persistence.*;

@Entity
@Table(name = "services_hospitaliers")
public class ServiceHospitalier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nom;

    public Integer getId() { return id; }
    public String getNom() { return nom; }
}