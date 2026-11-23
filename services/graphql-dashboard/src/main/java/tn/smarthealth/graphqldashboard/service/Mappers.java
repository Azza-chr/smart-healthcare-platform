package tn.smarthealth.graphqldashboard.service;

import tn.smarthealth.graphqldashboard.dto.AlerteView;
import tn.smarthealth.graphqldashboard.dto.ConstanteView;
import tn.smarthealth.graphqldashboard.dto.FicheView;
import tn.smarthealth.graphqldashboard.dto.PatientView;
import tn.smarthealth.graphqldashboard.dto.SoinView;
import tn.smarthealth.graphqldashboard.dto.TraitementView;
import tn.smarthealth.graphqldashboard.model.Alerte;
import tn.smarthealth.graphqldashboard.model.ConstanteVitale;
import tn.smarthealth.graphqldashboard.model.Dossier;
import tn.smarthealth.graphqldashboard.model.FicheIntervention;
import tn.smarthealth.graphqldashboard.model.Soin;
import tn.smarthealth.graphqldashboard.model.Traitement;

public final class Mappers {

    private Mappers() {
    }

    public static PatientView toView(Dossier d) {
        return new PatientView(d.getPatientId(), d.getNom(), d.getPrenom(), d.getDateNaissance().toString(),
                d.getService().getNom(), d.getPathologie(), d.getNiveauRisque(), d.getMedecinReferent());
    }

    public static ConstanteView toView(ConstanteVitale c) {
        Double temperature = c.getTemperature() == null ? null : c.getTemperature().doubleValue();
        return new ConstanteView(c.getMesureLe().toString(), c.getFrequenceCardiaque(), c.getSpo2(),
                temperature, c.getTensionSys(), c.getTensionDia());
    }

    public static AlerteView toView(Alerte a) {
        return new AlerteView(a.getId(), a.getType(), a.getNiveau(), a.getMessage(), a.getCreeLe().toString());
    }

    public static TraitementView toView(Traitement t) {
        return new TraitementView(t.getMedicament(), t.getPosologie(), t.getDebut().toString(),
                t.getFin() == null ? null : t.getFin().toString());
    }

    public static SoinView toView(Soin s) {
        return new SoinView(s.getDateSoin().toString(), s.getType(), s.getDescription(), s.getPraticien());
    }

    public static FicheView toView(FicheIntervention f) {
        return new FicheView(f.getId(), f.getPatientId(), f.getAlerteRef(), f.getResume(),
                f.getStatut(), f.getCreeLe().toString());
    }
}