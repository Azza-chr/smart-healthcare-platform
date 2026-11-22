package tn.smarthealth.restappointments.service;

import tn.smarthealth.restappointments.dto.CreneauDto;
import tn.smarthealth.restappointments.dto.RendezVousDto;
import tn.smarthealth.restappointments.model.Creneau;
import tn.smarthealth.restappointments.model.Medecin;
import tn.smarthealth.restappointments.model.Patient;
import tn.smarthealth.restappointments.model.RendezVous;

public final class Mappers {

    private Mappers() {
    }

    public static CreneauDto toDto(Creneau c) {
        Medecin m = c.getMedecin();
        return new CreneauDto(c.getId(), m.getId(), m.getNom(), m.getSpecialite(),
                c.getDebut(), c.getFin(), c.isUrgence(), c.getStatut());
    }

    public static RendezVousDto toDto(RendezVous r) {
        Patient p = r.getPatient();
        return new RendezVousDto(r.getId(), p.getId(), p.getPrenom() + " " + p.getNom(),
                toDto(r.getCreneau()), r.getMotif(), r.getType(), r.getStatut(), r.getCreeLe());
    }
}