package tn.smarthealth.authservice.model;

import jakarta.persistence.*;

@Entity
@Table(name = "utilisateurs")
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String username;
    private String passwordHash;
    private String nomComplet;
    private String role;
    private String patientId;   // null for staff accounts
    private boolean actif;

    public Integer getId() { return id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getNomComplet() { return nomComplet; }
    public String getRole() { return role; }
    public String getPatientId() { return patientId; }
    public boolean isActif() { return actif; }
}