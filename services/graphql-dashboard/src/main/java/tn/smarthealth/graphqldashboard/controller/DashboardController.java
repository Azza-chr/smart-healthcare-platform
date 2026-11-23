package tn.smarthealth.graphqldashboard.controller;

import graphql.schema.DataFetchingEnvironment;
import java.util.List;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import tn.smarthealth.graphqldashboard.dto.AlerteView;
import tn.smarthealth.graphqldashboard.dto.ConstanteView;
import tn.smarthealth.graphqldashboard.dto.FicheInterventionInput;
import tn.smarthealth.graphqldashboard.dto.FicheView;
import tn.smarthealth.graphqldashboard.dto.HistoriqueSoinsView;
import tn.smarthealth.graphqldashboard.dto.PatientView;
import tn.smarthealth.graphqldashboard.dto.TraitementView;
import tn.smarthealth.graphqldashboard.service.FicheService;
import tn.smarthealth.graphqldashboard.service.PatientService;

@Controller
public class DashboardController {

    private final PatientService patientService;
    private final FicheService ficheService;

    public DashboardController(PatientService patientService, FicheService ficheService) {
        this.patientService = patientService;
        this.ficheService = ficheService;
    }

    // ---------- Query ----------

    @QueryMapping
    public PatientView patient(@Argument String id) {
        return patientService.find(id).orElse(null);
    }

    @QueryMapping
    public List<PatientView> patients(DataFetchingEnvironment env) {
        // optional arguments: null when the client does not send them
        String service = env.getArgument("service");
        String pathologie = env.getArgument("pathologie");
        String risque = env.getArgument("risque");
        return patientService.search(service, pathologie, risque);
    }

    @QueryMapping
    public HistoriqueSoinsView historiqueSoins(@Argument String patientId, @Argument int jours) {
        return patientService.historique(patientId, jours);
    }

    // ---------- Mutation ----------

    @MutationMapping
    public FicheView creerFicheIntervention(@Argument FicheInterventionInput input) {
        return ficheService.creer(input);
    }

    // ---------- Nested fields: loaded only when the query asks for them ----------

    @SchemaMapping(typeName = "Patient", field = "derniereConstantes")
    public List<ConstanteView> derniereConstantes(PatientView patient, @Argument int limit) {
        return patientService.constantes(patient.id(), limit);
    }

    @SchemaMapping(typeName = "Patient", field = "alertesRecentes")
    public List<AlerteView> alertesRecentes(PatientView patient, @Argument int limit) {
        return patientService.alertes(patient.id(), limit);
    }

    @SchemaMapping(typeName = "Patient", field = "traitementsEnCours")
    public List<TraitementView> traitementsEnCours(PatientView patient) {
        return patientService.traitementsEnCours(patient.id());
    }

    @SchemaMapping(typeName = "FicheIntervention", field = "patient")
    public PatientView patientDeLaFiche(FicheView fiche) {
        return patientService.require(fiche.patientId());
    }
}