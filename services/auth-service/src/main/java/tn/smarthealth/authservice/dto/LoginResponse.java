package tn.smarthealth.authservice.dto;

public record LoginResponse(String token, String tokenType, long expiresIn,
                            String username, String nomComplet, String role, String patientId) {
}