package tn.smarthealth.restappointments.controller;

import java.net.URI;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tn.smarthealth.restappointments.dto.RendezVousDto;
import tn.smarthealth.restappointments.dto.RendezVousRequest;
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
        RendezVousDto created = rendezVousService.reserver(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/{id}")
    public RendezVousDto get(@PathVariable Integer id) {
        return rendezVousService.get(id);
    }
}