package tn.smarthealth.graphqldashboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.smarthealth.graphqldashboard.model.FicheIntervention;

public interface FicheInterventionRepository extends JpaRepository<FicheIntervention, Integer> {
}