package tn.smarthealth.graphqldashboard.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "constantes_vitales")
public class ConstanteVitale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String patientId;
    private LocalDateTime mesureLe;
    private Integer frequenceCardiaque;
    private Integer spo2;
    private BigDecimal temperature;
    private Integer tensionSys;
    private Integer tensionDia;

    public Long getId() { return id; }
    public String getPatientId() { return patientId; }
    public LocalDateTime getMesureLe() { return mesureLe; }
    public Integer getFrequenceCardiaque() { return frequenceCardiaque; }
    public Integer getSpo2() { return spo2; }
    public BigDecimal getTemperature() { return temperature; }
    public Integer getTensionSys() { return tensionSys; }
    public Integer getTensionDia() { return tensionDia; }
}