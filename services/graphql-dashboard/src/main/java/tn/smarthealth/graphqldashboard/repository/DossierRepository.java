package tn.smarthealth.graphqldashboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.smarthealth.graphqldashboard.model.Dossier;

public interface DossierRepository extends JpaRepository<Dossier, String> {
}