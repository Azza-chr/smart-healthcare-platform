\connect appointments_db

CREATE TABLE patients (
    id             VARCHAR(10) PRIMARY KEY,
    nom            VARCHAR(60) NOT NULL,
    prenom         VARCHAR(60) NOT NULL,
    date_naissance DATE        NOT NULL,
    email          VARCHAR(120),
    telephone      VARCHAR(20),
    adresse        VARCHAR(200)
);

CREATE TABLE medecins (
    id         SERIAL PRIMARY KEY,
    nom        VARCHAR(80) NOT NULL,
    specialite VARCHAR(40) NOT NULL
);

CREATE TABLE creneaux (
    id         SERIAL PRIMARY KEY,
    medecin_id INT NOT NULL REFERENCES medecins(id),
    debut      TIMESTAMP NOT NULL,
    fin        TIMESTAMP NOT NULL,
    urgence    BOOLEAN   NOT NULL DEFAULT FALSE,
    statut     VARCHAR(10) NOT NULL DEFAULT 'LIBRE' CHECK (statut IN ('LIBRE','OCCUPE'))
);
CREATE INDEX idx_creneaux_recherche ON creneaux (medecin_id, debut, statut);

CREATE TABLE rendez_vous (
    id         SERIAL PRIMARY KEY,
    patient_id VARCHAR(10) NOT NULL REFERENCES patients(id),
    creneau_id INT         NOT NULL REFERENCES creneaux(id),
    motif      VARCHAR(200),
    type       VARCHAR(10) NOT NULL DEFAULT 'NORMAL' CHECK (type IN ('NORMAL','URGENCE')),
    statut     VARCHAR(10) NOT NULL DEFAULT 'CONFIRME' CHECK (statut IN ('CONFIRME','ANNULE')),
    cree_le    TIMESTAMP   NOT NULL DEFAULT NOW()
);
-- Garantie en base : un créneau ne peut avoir qu'UN rendez-vous confirmé (sert à l'erreur 409)
CREATE UNIQUE INDEX uq_rdv_creneau_actif ON rendez_vous (creneau_id) WHERE statut = 'CONFIRME';

CREATE TABLE ordonnances (
    id              SERIAL PRIMARY KEY,
    patient_id      VARCHAR(10) NOT NULL REFERENCES patients(id),
    medecin_id      INT         NOT NULL REFERENCES medecins(id),
    date_ordonnance DATE        NOT NULL,
    medicament      VARCHAR(100) NOT NULL,
    posologie       VARCHAR(100) NOT NULL,
    duree_jours     INT          NOT NULL
);

INSERT INTO patients VALUES
 ('P001','Ben Salah','Ahmed',   '1958-03-14','ahmed.bensalah@mail.tn', '+216 20 111 001','12 Rue de Marseille, Tunis'),
 ('P002','Trabelsi', 'Sarra',   '1991-07-02','sarra.trabelsi@mail.tn', '+216 20 111 002','5 Avenue Habib Bourguiba, Ariana'),
 ('P003','Gharbi',   'Mohamed', '1969-11-23','mohamed.gharbi@mail.tn', '+216 20 111 003','27 Rue Ibn Khaldoun, Sousse'),
 ('P004','Mansouri', 'Leila',   '1975-05-30','leila.mansouri@mail.tn', '+216 20 111 004','8 Rue de Carthage, La Marsa'),
 ('P005','Jebali',   'Karim',   '1999-01-09','karim.jebali@mail.tn',   '+216 20 111 005','3 Rue Farhat Hached, Sfax');

INSERT INTO medecins (nom, specialite) VALUES
 ('Dr Nadia Khelifi',   'Cardiologie'),
 ('Dr Hichem Chaabane', 'Pneumologie');

-- Créneaux normaux : 7 prochains jours, 6 créneaux de 30 min par jour et par médecin
INSERT INTO creneaux (medecin_id, debut, fin, urgence)
SELECT m.id,
       (CURRENT_DATE + d) + h * INTERVAL '1 hour',
       (CURRENT_DATE + d) + h * INTERVAL '1 hour' + INTERVAL '30 minutes',
       FALSE
FROM medecins m, generate_series(1,7) AS d, unnest(ARRAY[9,10,11,14,15,16]) AS h;

-- Créneaux d'URGENCE : dans les 3 prochaines heures, pour chaque médecin
INSERT INTO creneaux (medecin_id, debut, fin, urgence)
SELECT m.id,
       date_trunc('hour', NOW()) + n * INTERVAL '1 hour',
       date_trunc('hour', NOW()) + n * INTERVAL '1 hour' + INTERVAL '30 minutes',
       TRUE
FROM medecins m, generate_series(1,3) AS n;

-- Un rendez-vous déjà existant : Ahmed chez le Dr Khelifi (demain, premier créneau)
WITH c AS (
    SELECT id FROM creneaux
    WHERE medecin_id = 1 AND urgence = FALSE
    ORDER BY debut LIMIT 1
)
INSERT INTO rendez_vous (patient_id, creneau_id, motif)
SELECT 'P001', id, 'Contrôle cardiologique' FROM c;
UPDATE creneaux SET statut = 'OCCUPE' WHERE id IN (SELECT creneau_id FROM rendez_vous);

INSERT INTO ordonnances (patient_id, medecin_id, date_ordonnance, medicament, posologie, duree_jours) VALUES
 ('P001',1,CURRENT_DATE - 20,'Bisoprolol 5 mg',   '1 comprimé le matin',          90),
 ('P001',1,CURRENT_DATE - 20,'Furosémide 40 mg',  '1 comprimé le matin',          90),
 ('P001',1,CURRENT_DATE - 20,'Ramipril 5 mg',     '1 comprimé le soir',           90),
 ('P002',2,CURRENT_DATE - 40,'Salbutamol spray',  '2 bouffées si gêne respiratoire', 60),
 ('P002',2,CURRENT_DATE - 40,'Budésonide inhalé', '1 inhalation matin et soir',   60),
 ('P003',1,CURRENT_DATE - 60,'Amlodipine 5 mg',   '1 comprimé par jour',         120);
