package tn.smarthealth.graphqldashboard.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.smarthealth.graphqldashboard.dto.FicheInterventionInput;
import tn.smarthealth.graphqldashboard.dto.FicheView;
import tn.smarthealth.graphqldashboard.exception.BadRequestException;
import tn.smarthealth.graphqldashboard.exception.ResourceNotFoundException;
import tn.smarthealth.graphqldashboard.model.FicheIntervention;
import tn.smarthealth.graphqldashboard.repository.DossierRepository;
import tn.smarthealth.graphqldashboard.repository.FicheInterventionRepository;

@Service
public class FicheService {

    private final FicheInterventionRepository ficheRepository;
    private final DossierRepository dossierRepository;

    public FicheService(FicheInterventionRepository ficheRepository, DossierRepository dossierRepository) {
        this.ficheRepository = ficheRepository;
        this.dossierRepository = dossierRepository;
    }

    @Transactional
    public FicheView creer(FicheInterventionInput input) {
        if (input.patientId() == null || input.patientId().isBlank()) {
            throw new BadRequestException("patientId is required");
        }
        if (input.resume() == null || input.resume().isBlank()) {
            throw new BadRequestException("resume must not be blank");
        }
        if (input.resume().length() > 2000) {
            throw new BadRequestException("resume is limited to 2000 characters");
        }
        if (!dossierRepository.existsById(input.patientId())) {
            throw new ResourceNotFoundException("Patient " + input.patientId() + " not found");
        }

        FicheIntervention fiche = new FicheIntervention();
        fiche.setPatientId(input.patientId());
        fiche.setAlerteRef(input.alerteRef());
        fiche.setResume(input.resume());
        ficheRepository.save(fiche);
        return Mappers.toView(fiche);
    }
}