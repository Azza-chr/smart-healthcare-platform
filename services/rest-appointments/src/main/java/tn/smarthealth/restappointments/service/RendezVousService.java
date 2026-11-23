package tn.smarthealth.restappointments.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.smarthealth.restappointments.dto.RendezVousDto;
import tn.smarthealth.restappointments.dto.RendezVousRequest;
import tn.smarthealth.restappointments.dto.RendezVousUpdateRequest;
import tn.smarthealth.restappointments.dto.UrgenceRequest;
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

    private static final String DEFAULT_URGENCE_SPECIALTY = "Cardiologie";

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
        Patient patient = trouverPatient(request.patientId());
        Creneau creneau = verrouiller(request.creneauId());
        verifierReservable(creneau);

        RendezVous rendezVous = nouveau(patient, creneau, request.motif(), "NORMAL");
        rendezVousRepository.save(rendezVous);
        return Mappers.toDto(rendezVous);
    }

    @Transactional(readOnly = true)
    public RendezVousDto get(Integer id) {
        return Mappers.toDto(trouver(id));
    }

    @Transactional
    public RendezVousDto modifier(Integer id, RendezVousUpdateRequest request) {
        RendezVous rendezVous = trouver(id);
        if ("ANNULE".equals(rendezVous.getStatut())) {
            throw new ConflictException("Appointment " + id + " is cancelled and cannot be modified");
        }
        if ("URGENCE".equals(rendezVous.getType())) {
            throw new BadRequestException("Emergency appointments cannot be modified");
        }

        if (!rendezVous.getCreneau().getId().equals(request.creneauId())) {
            Creneau nouveauCreneau = verrouiller(request.creneauId());
            verifierReservable(nouveauCreneau);

            Creneau ancienCreneau = rendezVous.getCreneau();
            ancienCreneau.setStatut("LIBRE");
            nouveauCreneau.setStatut("OCCUPE");
            rendezVous.setCreneau(nouveauCreneau);
        }
        if (request.motif() != null && !request.motif().isBlank()) {
            rendezVous.setMotif(request.motif());
        }
        return Mappers.toDto(rendezVous);
    }

    @Transactional
    public void annuler(Integer id) {
        RendezVous rendezVous = trouver(id);
        if ("ANNULE".equals(rendezVous.getStatut())) {
            throw new ConflictException("Appointment " + id + " is already cancelled");
        }
        Creneau creneau = verrouiller(rendezVous.getCreneau().getId());
        rendezVous.setStatut("ANNULE");
        creneau.setStatut("LIBRE");
    }

    @Transactional
    public RendezVousDto urgence(UrgenceRequest request) {
        Patient patient = trouverPatient(request.patientId());
        String specialite = (request.specialite() == null || request.specialite().isBlank())
                ? DEFAULT_URGENCE_SPECIALTY : request.specialite().trim();

        List<Integer> candidats = creneauRepository.findUrgenceLibreIds(specialite, PageRequest.of(0, 5));
        LocalDateTime debut = LocalDateTime.now().plusMinutes(10).withSecond(0).withNano(0);

        for (Integer candidatId : candidats) {
            Creneau creneau = verrouiller(candidatId);
            if ("LIBRE".equals(creneau.getStatut())) {
                // The emergency slot is reserved capacity: its time is set when it is used
                creneau.setDebut(debut);
                creneau.setFin(debut.plusMinutes(30));
                creneau.setStatut("OCCUPE");

                RendezVous rendezVous = nouveau(patient, creneau, request.motif(), "URGENCE");
                rendezVousRepository.save(rendezVous);
                return Mappers.toDto(rendezVous);
            }
        }
        throw new ConflictException("No emergency slot available for " + specialite);
    }

    // ---------- helpers ----------

    private RendezVous trouver(Integer id) {
        return rendezVousRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment " + id + " not found"));
    }

    private Patient trouverPatient(String id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient " + id + " not found"));
    }

    private Creneau verrouiller(Integer id) {
        return creneauRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Slot " + id + " not found"));
    }

    private void verifierReservable(Creneau creneau) {
        if (creneau.isUrgence()) {
            throw new BadRequestException("Emergency slots can only be booked through /rendez-vous/urgence");
        }
        if (creneau.getDebut().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("This slot is in the past");
        }
        if (!"LIBRE".equals(creneau.getStatut())) {
            throw new ConflictException("Slot " + creneau.getId() + " is already booked");
        }
    }

    private RendezVous nouveau(Patient patient, Creneau creneau, String motif, String type) {
        RendezVous rendezVous = new RendezVous();
        rendezVous.setPatient(patient);
        rendezVous.setCreneau(creneau);
        rendezVous.setMotif(motif);
        rendezVous.setType(type);
        creneau.setStatut("OCCUPE");
        return rendezVous;
    }
}