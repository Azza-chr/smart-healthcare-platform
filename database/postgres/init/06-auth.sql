\connect auth_db
CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE utilisateurs (
    id            SERIAL PRIMARY KEY,
    username      VARCHAR(40) UNIQUE NOT NULL,
    password_hash VARCHAR(100) NOT NULL,            -- BCrypt, jamais en clair
    nom_complet   VARCHAR(80)  NOT NULL,
    role          VARCHAR(10)  NOT NULL CHECK (role IN ('PATIENT','MEDECIN','INFIRMIER','ADMIN')),
    patient_id    VARCHAR(10),                      -- renseigné uniquement pour les patients
    actif         BOOLEAN NOT NULL DEFAULT TRUE
);

-- Mot de passe de TOUS les comptes de démonstration : demo1234
INSERT INTO utilisateurs (username, password_hash, nom_complet, role, patient_id) VALUES
 ('sarra',      crypt('demo1234', gen_salt('bf', 10)), 'Sarra Trabelsi',     'PATIENT',   'P002'),
 ('ahmed',      crypt('demo1234', gen_salt('bf', 10)), 'Ahmed Ben Salah',    'PATIENT',   'P001'),
 ('mohamed',    crypt('demo1234', gen_salt('bf', 10)), 'Mohamed Gharbi',     'PATIENT',   'P003'),
 ('leila',      crypt('demo1234', gen_salt('bf', 10)), 'Leila Mansouri',     'PATIENT',   'P004'),
 ('karim',      crypt('demo1234', gen_salt('bf', 10)), 'Karim Jebali',       'PATIENT',   'P005'),
 ('dr.khelifi', crypt('demo1234', gen_salt('bf', 10)), 'Dr Nadia Khelifi',   'MEDECIN',   NULL),
 ('inf.hamdi',  crypt('demo1234', gen_salt('bf', 10)), 'Walid Hamdi',        'INFIRMIER', NULL),
 ('admin',      crypt('demo1234', gen_salt('bf', 10)), 'Admin Hôpital',      'ADMIN',     NULL);
