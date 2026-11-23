package tn.smarthealth.graphqldashboard.repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.smarthealth.graphqldashboard.model.Alerte;

public interface AlerteRepository extends JpaRepository<Alerte, Integer> {
    List<Alerte> findByPatientIdOrderByCreeLeDesc(String patientId, Pageable pageable);
}