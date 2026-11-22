package tn.smarthealth.restappointments.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.smarthealth.restappointments.dto.CreneauDto;
import tn.smarthealth.restappointments.repository.CreneauRepository;

@Service
public class CreneauService {

    private final CreneauRepository creneauRepository;

    public CreneauService(CreneauRepository creneauRepository) {
        this.creneauRepository = creneauRepository;
    }

    @Transactional(readOnly = true)
    public List<CreneauDto> libres(String specialite, LocalDate date) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime min = now;
        LocalDateTime max = now.plusDays(30);
        if (date != null) {
            LocalDateTime start = date.atStartOfDay();
            min = start.isAfter(now) ? start : now;
            max = date.plusDays(1).atStartOfDay();
        }
        String filter = (specialite == null || specialite.isBlank()) ? "%" : specialite.trim();
        return creneauRepository.findLibres(filter, min, max).stream().map(Mappers::toDto).toList();
    }
}