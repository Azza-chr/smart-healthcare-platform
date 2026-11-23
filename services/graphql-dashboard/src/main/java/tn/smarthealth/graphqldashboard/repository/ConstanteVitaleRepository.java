package tn.smarthealth.graphqldashboard.repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.smarthealth.graphqldashboard.model.ConstanteVitale;

public interface ConstanteVitaleRepository extends JpaRepository<ConstanteVitale, Long> {
    List<ConstanteVitale> findByPatientIdOrderByMesureLeDesc(String patientId, Pageable pageable);
}