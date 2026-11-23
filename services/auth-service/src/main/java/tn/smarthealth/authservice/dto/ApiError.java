package tn.smarthealth.authservice.dto;

public record ApiError(int status, String message, String timestamp) {
}