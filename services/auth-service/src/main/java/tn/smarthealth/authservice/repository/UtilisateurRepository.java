package tn.smarthealth.authservice.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.smarthealth.authservice.model.Utilisateur;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Integer> {
    Optional<Utilisateur> findByUsername(String username);
}