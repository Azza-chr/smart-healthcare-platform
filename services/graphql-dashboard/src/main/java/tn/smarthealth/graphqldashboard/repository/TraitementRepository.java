package tn.smarthealth.graphqldashboard.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.smarthealth.graphqldashboard.model.Traitement;

public interface TraitementRepository extends JpaRepository<Traitement, Integer> {

    @Query("""
            select t from Traitement t
            where t.patientId = :patientId and (t.fin is null or t.fin >= :today)
            order by t.debut desc
            """)
    List<Traitement> findEnCours(@Param("patientId") String patientId, @Param("today") LocalDate today);
}