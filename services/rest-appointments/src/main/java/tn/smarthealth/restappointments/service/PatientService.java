package tn.smarthealth.restappointments.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.smarthealth.restappointments.dto.OrdonnanceDto;
import tn.smarthealth.restappointments.dto.PatientDto;
import tn.smarthealth.restappointments.dto.PatientUpdateRequest;
import tn.smarthealth.restappointments.exception.BadRequestException;
import tn.smarthealth.restappointments.exception.ResourceNotFoundException;
import tn.smarthealth.restappointments.model.Patient;
import tn.smarthealth.restappointments.repository.OrdonnanceRepository;
import tn.smarthealth.restappointments.repository.PatientRepository;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final OrdonnanceRepository ordonnanceRepository;

    public PatientService(PatientRepository patientRepository, OrdonnanceRepository ordonnanceRepository) {
        this.patientRepository = patientRepository;
        this.ordonnanceRepository = ordonnanceRepository;
    }

    @Transactional(readOnly = true)
    public PatientDto get(String id) {
        return Mappers.toDto(trouver(id));
    }

    @Transactional
    public PatientDto update(String id, PatientUpdateRequest request) {
        if (request.email() == null && request.telephone() == null && request.adresse() == null) {
            throw new BadRequestException("Nothing to update: send email, telephone and/or adresse");
        }
        Patient patient = trouver(id);
        if (request.email() != null) {
            patient.setEmail(request.email());
        }
        if (request.telephone() != null) {
            patient.setTelephone(request.telephone());
        }
        if (request.adresse() != null) {
            patient.setAdresse(request.adresse());
        }
        return Mappers.toDto(patient);
    }

    @Transactional(readOnly = true)
    public List<OrdonnanceDto> ordonnances(String patientId) {
        trouver(patientId);
        return ordonnanceRepository.findByPatientIdOrderByDateOrdonnanceDesc(patientId).stream()
                .map(Mappers::toDto).toList();
    }

    private Patient trouver(String id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient " + id + " not found"));
    }
}