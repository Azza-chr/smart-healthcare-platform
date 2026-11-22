package tn.smarthealth.restappointments.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.smarthealth.restappointments.model.Ordonnance;
import java.util.List;

public interface OrdonnanceRepository extends JpaRepository<Ordonnance, Integer> {
    List<Ordonnance> findByPatientIdOrderByDateOrdonnanceDesc(String patientId);
}