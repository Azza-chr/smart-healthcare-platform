package tn.smarthealth.restappointments.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.smarthealth.restappointments.model.Creneau;

public interface CreneauRepository extends JpaRepository<Creneau, Integer> {

    @Query("""
            select c from Creneau c join fetch c.medecin m
            where c.statut = 'LIBRE' and c.urgence = false
              and lower(m.specialite) like lower(:specialite)
              and c.debut >= :debutMin and c.debut < :debutMax
            order by c.debut
            """)
    List<Creneau> findLibres(@Param("specialite") String specialite,
                             @Param("debutMin") LocalDateTime debutMin,
                             @Param("debutMax") LocalDateTime debutMax);

    // Candidate emergency slots (ids only; each one is then locked individually)
    @Query("""
            select c.id from Creneau c join c.medecin m
            where c.statut = 'LIBRE' and c.urgence = true
              and lower(m.specialite) like lower(:specialite)
            order by c.id
            """)
    List<Integer> findUrgenceLibreIds(@Param("specialite") String specialite, Pageable pageable);

    // Locks the row until the end of the transaction: two people cannot take the same slot at once
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Creneau c where c.id = :id")
    Optional<Creneau> findByIdForUpdate(@Param("id") Integer id);
}