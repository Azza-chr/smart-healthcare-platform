package tn.smarthealth.graphqldashboard.dto;

public record ConstanteView(String mesureLe, Integer frequenceCardiaque, Integer spo2, Double temperature,
                            Integer tensionSys, Integer tensionDia) {
}