-- Une base par microservice (principe : un service ne lit jamais la base d'un autre)
CREATE DATABASE insurance_db;      -- Service SOAP   : assurance / dossier certifié
CREATE DATABASE appointments_db;   -- Service REST   : rendez-vous, ordonnances, profil
CREATE DATABASE dashboard_db;      -- Service GraphQL: tableau de bord médical
CREATE DATABASE telemetry_db;      -- Service gRPC   : capteurs, mesures, alertes
CREATE DATABASE orchestrator_db;   -- Orchestrateur  : journal des workflows, interventions
CREATE DATABASE auth_db;           -- Service Auth   : comptes et rôles (JWT)
