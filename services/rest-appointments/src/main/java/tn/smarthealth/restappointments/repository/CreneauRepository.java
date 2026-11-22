package tn.smarthealth.restappointments.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.smarthealth.restappointments.model.Creneau;

public interface CreneauRepository extends JpaRepository<Creneau, Integer> {
}