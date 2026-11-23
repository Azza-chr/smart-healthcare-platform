package tn.smarthealth.graphqldashboard.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.smarthealth.graphqldashboard.model.Soin;

public interface SoinRepository extends JpaRepository<Soin, Integer> {
    List<Soin> findByPatientIdAndDateSoinGreaterThanEqualOrderByDateSoinDesc(String patientId, LocalDate since);
}