package tn.smarthealth.graphqldashboard.dto;

import java.util.List;

public record HistoriqueSoinsView(String patientId, int jours, int total,
                                  List<CompteParType> parType, List<SoinView> soins) {
}