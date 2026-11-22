package tn.smarthealth.restappointments.controller;

import java.net.URI;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tn.smarthealth.restappointments.dto.RendezVousDto;
import tn.smarthealth.restappointments.dto.RendezVousRequest;
import tn.smarthealth.restappointments.dto.RendezVousUpdateRequest;
import tn.smarthealth.restappointments.dto.UrgenceRequest;
import tn.smarthealth.restappointments.service.RendezVousService;

@RestController
@RequestMapping("/rendez-vous")
public class RendezVousController {

    private final RendezVousService rendezVousService;

    public RendezVousController(RendezVousService rendezVousService) {
        this.rendezVousService = rendezVousService;
    }

    @PostMapping
    public ResponseEntity<RendezVousDto> reserver(@Valid @RequestBody RendezVousRequest request) {
        return created(rendezVousService.reserver(request));
    }

    @PostMapping("/urgence")
    public ResponseEntity<RendezVousDto> urgence(@Valid @RequestBody UrgenceRequest request) {
        return created(rendezVousService.urgence(request));
    }

    @GetMapping("/{id}")
    public RendezVousDto get(@PathVariable Integer id) {
        return rendezVousService.get(id);
    }

    @PutMapping("/{id}")
    public RendezVousDto modifier(@PathVariable Integer id,
                                  @Valid @RequestBody RendezVousUpdateRequest request) {
        return rendezVousService.modifier(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> annuler(@PathVariable Integer id) {
        rendezVousService.annuler(id);
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<RendezVousDto> created(RendezVousDto dto) {
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/rendez-vous/{id}").buildAndExpand(dto.id()).toUri();
        return ResponseEntity.created(location).body(dto);
    }
}