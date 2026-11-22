package tn.smarthealth.restappointments.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.smarthealth.restappointments.model.Medecin;

public interface MedecinRepository extends JpaRepository<Medecin, Integer> {
}