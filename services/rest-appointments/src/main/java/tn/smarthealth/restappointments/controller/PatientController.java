package tn.smarthealth.restappointments.controller;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.smarthealth.restappointments.dto.OrdonnanceDto;
import tn.smarthealth.restappointments.dto.PatientDto;
import tn.smarthealth.restappointments.dto.PatientUpdateRequest;
import tn.smarthealth.restappointments.service.PatientService;

@RestController
@RequestMapping("/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping("/{id}")
    public PatientDto get(@PathVariable String id) {
        return patientService.get(id);
    }

    @PutMapping("/{id}")
    public PatientDto update(@PathVariable String id, @Valid @RequestBody PatientUpdateRequest request) {
        return patientService.update(id, request);
    }

    @GetMapping("/{id}/ordonnances")
    public List<OrdonnanceDto> ordonnances(@PathVariable String id) {
        return patientService.ordonnances(id);
    }
}