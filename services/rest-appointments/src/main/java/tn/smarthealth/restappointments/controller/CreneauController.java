package tn.smarthealth.restappointments.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tn.smarthealth.restappointments.dto.CreneauDto;
import tn.smarthealth.restappointments.service.CreneauService;

@RestController
public class CreneauController {

    private final CreneauService creneauService;

    public CreneauController(CreneauService creneauService) {
        this.creneauService = creneauService;
    }

    @GetMapping("/creneaux")
    public List<CreneauDto> libres(
            @RequestParam(required = false) String specialite,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return creneauService.libres(specialite, date);
    }
}