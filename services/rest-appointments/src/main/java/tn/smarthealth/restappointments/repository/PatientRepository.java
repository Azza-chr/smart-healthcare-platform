package tn.smarthealth.restappointments.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.smarthealth.restappointments.model.Patient;

public interface PatientRepository extends JpaRepository<Patient, String> {
}