package tn.smarthealth.authservice.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.smarthealth.authservice.dto.LoginRequest;
import tn.smarthealth.authservice.dto.LoginResponse;
import tn.smarthealth.authservice.exception.AuthenticationFailedException;
import tn.smarthealth.authservice.model.Utilisateur;
import tn.smarthealth.authservice.repository.UtilisateurRepository;

@Service
public class AuthService {

    private final UtilisateurRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UtilisateurRepository repository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        Utilisateur user = repository.findByUsername(request.username().trim())
                .filter(Utilisateur::isActif)
                .filter(u -> passwordEncoder.matches(request.password(), u.getPasswordHash()))
                .orElseThrow(() -> new AuthenticationFailedException("Invalid username or password"));

        return new LoginResponse(jwtService.generate(user), "Bearer", jwtService.getExpirationSeconds(),
                user.getUsername(), user.getNomComplet(), user.getRole(), user.getPatientId());
    }
}