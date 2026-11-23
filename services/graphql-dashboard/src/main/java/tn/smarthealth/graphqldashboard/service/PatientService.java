package tn.smarthealth.graphqldashboard.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.smarthealth.graphqldashboard.dto.AlerteView;
import tn.smarthealth.graphqldashboard.dto.CompteParType;
import tn.smarthealth.graphqldashboard.dto.ConstanteView;
import tn.smarthealth.graphqldashboard.dto.HistoriqueSoinsView;
import tn.smarthealth.graphqldashboard.dto.PatientView;
import tn.smarthealth.graphqldashboard.dto.SoinView;
import tn.smarthealth.graphqldashboard.dto.TraitementView;
import tn.smarthealth.graphqldashboard.exception.BadRequestException;
import tn.smarthealth.graphqldashboard.exception.ResourceNotFoundException;
import tn.smarthealth.graphqldashboard.model.Dossier;
import tn.smarthealth.graphqldashboard.model.ServiceHospitalier;
import tn.smarthealth.graphqldashboard.model.Soin;
import tn.smarthealth.graphqldashboard.repository.AlerteRepository;
import tn.smarthealth.graphqldashboard.repository.ConstanteVitaleRepository;
import tn.smarthealth.graphqldashboard.repository.DossierRepository;
import tn.smarthealth.graphqldashboard.repository.SoinRepository;
import tn.smarthealth.graphqldashboard.repository.TraitementRepository;

@Service
public class PatientService {

    private static final int MAX_LIMIT = 50;

    @PersistenceContext
    private EntityManager em;

    private final DossierRepository dossierRepository;
    private final ConstanteVitaleRepository constanteRepository;
    private final AlerteRepository alerteRepository;
    private final TraitementRepository traitementRepository;
    private final SoinRepository soinRepository;

    public PatientService(DossierRepository dossierRepository,
                          ConstanteVitaleRepository constanteRepository,
                          AlerteRepository alerteRepository,
                          TraitementRepository traitementRepository,
                          SoinRepository soinRepository) {
        this.dossierRepository = dossierRepository;
        this.constanteRepository = constanteRepository;
        this.alerteRepository = alerteRepository;
        this.traitementRepository = traitementRepository;
        this.soinRepository = soinRepository;
    }

    @Transactional(readOnly = true)
    public Optional<PatientView> find(String id) {
        return dossierRepository.findById(id).map(Mappers::toView);
    }

    @Transactional(readOnly = true)
    public PatientView require(String id) {
        return find(id).orElseThrow(() -> new ResourceNotFoundException("Patient " + id + " not found"));
    }

    // Dynamic filtering: only the filters that are provided are added to the query
    @Transactional(readOnly = true)
    public List<PatientView> search(String service, String pathologie, String risque) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Dossier> query = cb.createQuery(Dossier.class);
        Root<Dossier> dossier = query.from(Dossier.class);
        Join<Dossier, ServiceHospitalier> serviceJoin = dossier.join("service");

        List<Predicate> filters = new ArrayList<>();
        if (service != null && !service.isBlank()) {
            filters.add(cb.equal(cb.lower(serviceJoin.<String>get("nom")), service.trim().toLowerCase(Locale.ROOT)));
        }
        if (pathologie != null && !pathologie.isBlank()) {
            filters.add(cb.like(cb.lower(dossier.<String>get("pathologie")),
                    "%" + pathologie.trim().toLowerCase(Locale.ROOT) + "%"));
        }
        if (risque != null && !risque.isBlank()) {
            filters.add(cb.equal(dossier.get("niveauRisque"), risque));
        }

        query.select(dossier)
                .where(filters.toArray(new Predicate[0]))
                .orderBy(cb.asc(dossier.get("nom")), cb.asc(dossier.get("prenom")));

        return em.createQuery(query).getResultList().stream().map(Mappers::toView).toList();
    }

    @Transactional(readOnly = true)
    public List<ConstanteView> constantes(String patientId, int limit) {
        return constanteRepository.findByPatientIdOrderByMesureLeDesc(patientId, PageRequest.of(0, clamp(limit)))
                .stream().map(Mappers::toView).toList();
    }

    @Transactional(readOnly = true)
    public List<AlerteView> alertes(String patientId, int limit) {
        return alerteRepository.findByPatientIdOrderByCreeLeDesc(patientId, PageRequest.of(0, clamp(limit)))
                .stream().map(Mappers::toView).toList();
    }

    @Transactional(readOnly = true)
    public List<TraitementView> traitementsEnCours(String patientId) {
        return traitementRepository.findEnCours(patientId, LocalDate.now())
                .stream().map(Mappers::toView).toList();
    }

    // Care history over the last N days, aggregated by type, computed on demand
    @Transactional(readOnly = true)
    public HistoriqueSoinsView historique(String patientId, int jours) {
        if (jours < 1 || jours > 3650) {
            throw new BadRequestException("jours must be between 1 and 3650");
        }
        if (!dossierRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient " + patientId + " not found");
        }
        List<Soin> soins = soinRepository.findByPatientIdAndDateSoinGreaterThanEqualOrderByDateSoinDesc(
                patientId, LocalDate.now().minusDays(jours));

        List<CompteParType> parType = soins.stream()
                .collect(Collectors.groupingBy(Soin::getType, TreeMap::new, Collectors.counting()))
                .entrySet().stream()
                .map(e -> new CompteParType(e.getKey(), e.getValue().intValue()))
                .toList();
        List<SoinView> vues = soins.stream().map(Mappers::toView).toList();
        return new HistoriqueSoinsView(patientId, jours, soins.size(), parType, vues);
    }

    private int clamp(int limit) {
        return Math.max(1, Math.min(limit, MAX_LIMIT));
    }
}