package tn.smarthealth.restappointments.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.smarthealth.restappointments.dto.RendezVousDto;
import tn.smarthealth.restappointments.dto.RendezVousRequest;
import tn.smarthealth.restappointments.exception.BadRequestException;
import tn.smarthealth.restappointments.exception.ConflictException;
import tn.smarthealth.restappointments.exception.ResourceNotFoundException;
import tn.smarthealth.restappointments.model.Creneau;
import tn.smarthealth.restappointments.model.Patient;
import tn.smarthealth.restappointments.model.RendezVous;
import tn.smarthealth.restappointments.repository.CreneauRepository;
import tn.smarthealth.restappointments.repository.PatientRepository;
import tn.smarthealth.restappointments.repository.RendezVousRepository;

@Service
public class RendezVousService {

    private final RendezVousRepository rendezVousRepository;
    private final CreneauRepository creneauRepository;
    private final PatientRepository patientRepository;

    public RendezVousService(RendezVousRepository rendezVousRepository,
                             CreneauRepository creneauRepository,
                             PatientRepository patientRepository) {
        this.rendezVousRepository = rendezVousRepository;
        this.creneauRepository = creneauRepository;
        this.patientRepository = patientRepository;
    }

    @Transactional
    public RendezVousDto reserver(RendezVousRequest request) {
        Patient patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient " + request.patientId() + " not found"));

        Creneau creneau = creneauRepository.findByIdForUpdate(request.creneauId())
                .orElseThrow(() -> new ResourceNotFoundException("Slot " + request.creneauId() + " not found"));

        if (creneau.isUrgence()) {
            throw new BadRequestException("Emergency slots can only be booked through /rendez-vous/urgence");
        }
        if (creneau.getDebut().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("This slot is in the past");
        }
        if (!"LIBRE".equals(creneau.getStatut())) {
            throw new ConflictException("Slot " + creneau.getId() + " is already booked");
        }

        RendezVous rendezVous = new RendezVous();
        rendezVous.setPatient(patient);
        rendezVous.setCreneau(creneau);
        rendezVous.setMotif(request.motif());
        creneau.setStatut("OCCUPE");

        rendezVousRepository.save(rendezVous);
        return Mappers.toDto(rendezVous);
    }

    @Transactional(readOnly = true)
    public RendezVousDto get(Integer id) {
        return rendezVousRepository.findById(id)
                .map(Mappers::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment " + id + " not found"));
    }
}