package tn.smarthealth.restappointments.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.smarthealth.restappointments.model.RendezVous;

public interface RendezVousRepository extends JpaRepository<RendezVous, Integer> {
}