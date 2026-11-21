\connect dashboard_db

CREATE TABLE services_hospitaliers (
    id  SERIAL PRIMARY KEY,
    nom VARCHAR(40) UNIQUE NOT NULL
);

CREATE TABLE dossiers (
    patient_id        VARCHAR(10) PRIMARY KEY,
    nom               VARCHAR(60) NOT NULL,
    prenom            VARCHAR(60) NOT NULL,
    date_naissance    DATE        NOT NULL,
    service_id        INT         NOT NULL REFERENCES services_hospitaliers(id),
    pathologie        VARCHAR(60) NOT NULL,
    niveau_risque     VARCHAR(10) NOT NULL CHECK (niveau_risque IN ('FAIBLE','MOYEN','ELEVE')),
    medecin_referent  VARCHAR(80) NOT NULL
);
CREATE INDEX idx_dossiers_filtres ON dossiers (service_id, pathologie, niveau_risque);

CREATE TABLE constantes_vitales (
    id           BIGSERIAL PRIMARY KEY,
    patient_id   VARCHAR(10) NOT NULL REFERENCES dossiers(patient_id),
    mesure_le    TIMESTAMP   NOT NULL,
    frequence_cardiaque INT,
    spo2         INT,
    temperature  NUMERIC(4,1),
    tension_sys  INT,
    tension_dia  INT
);
CREATE INDEX idx_constantes_patient ON constantes_vitales (patient_id, mesure_le DESC);

CREATE TABLE alertes (
    id         SERIAL PRIMARY KEY,
    patient_id VARCHAR(10) NOT NULL REFERENCES dossiers(patient_id),
    type       VARCHAR(30) NOT NULL,
    niveau     VARCHAR(10) NOT NULL CHECK (niveau IN ('MOYEN','CRITIQUE')),
    message    VARCHAR(200) NOT NULL,
    cree_le    TIMESTAMP   NOT NULL
);

CREATE TABLE traitements (
    id         SERIAL PRIMARY KEY,
    patient_id VARCHAR(10) NOT NULL REFERENCES dossiers(patient_id),
    medicament VARCHAR(100) NOT NULL,
    posologie  VARCHAR(100) NOT NULL,
    debut      DATE NOT NULL,
    fin        DATE           -- NULL = traitement en cours
);

CREATE TABLE soins (
    id          SERIAL PRIMARY KEY,
    patient_id  VARCHAR(10) NOT NULL REFERENCES dossiers(patient_id),
    date_soin   DATE        NOT NULL,
    type        VARCHAR(40) NOT NULL,
    description VARCHAR(200) NOT NULL,
    praticien   VARCHAR(80) NOT NULL
);
CREATE INDEX idx_soins_patient ON soins (patient_id, date_soin DESC);

-- Fiches d'intervention créées par l'orchestrateur (mutation GraphQL)
CREATE TABLE fiches_intervention (
    id            SERIAL PRIMARY KEY,
    patient_id    VARCHAR(10) NOT NULL REFERENCES dossiers(patient_id),
    alerte_ref    VARCHAR(40),
    resume        TEXT NOT NULL,
    statut        VARCHAR(20) NOT NULL DEFAULT 'OUVERTE',
    cree_le       TIMESTAMP NOT NULL DEFAULT NOW()
);

INSERT INTO services_hospitaliers (nom) VALUES
 ('Cardiologie'),('Pneumologie'),('Diabétologie'),('Neurologie');

INSERT INTO dossiers (patient_id, nom, prenom, date_naissance, service_id, pathologie, niveau_risque, medecin_referent) VALUES
 ('P001','Ben Salah','Ahmed',   '1958-03-14',(SELECT id FROM services_hospitaliers WHERE nom='Cardiologie'), 'Insuffisance cardiaque','ELEVE', 'Dr Nadia Khelifi'),
 ('P002','Trabelsi', 'Sarra',   '1991-07-02',(SELECT id FROM services_hospitaliers WHERE nom='Pneumologie'), 'Asthme',                'MOYEN', 'Dr Hichem Chaabane'),
 ('P003','Gharbi',   'Mohamed', '1969-11-23',(SELECT id FROM services_hospitaliers WHERE nom='Cardiologie'), 'Hypertension',          'MOYEN', 'Dr Nadia Khelifi'),
 ('P004','Mansouri', 'Leila',   '1975-05-30',(SELECT id FROM services_hospitaliers WHERE nom='Diabétologie'),'Diabète type 2',        'FAIBLE','Dr Amel Ben Youssef'),
 ('P005','Jebali',   'Karim',   '1999-01-09',(SELECT id FROM services_hospitaliers WHERE nom='Neurologie'),  'Épilepsie',             'ELEVE', 'Dr Sami Rekik');

-- Historique de constantes : 5 jours, une mesure toutes les 3 heures, pour chaque patient
INSERT INTO constantes_vitales (patient_id, mesure_le, frequence_cardiaque, spo2, temperature, tension_sys, tension_dia)
SELECT d.patient_id,
       NOW() - (i * INTERVAL '3 hours'),
       CASE d.patient_id WHEN 'P001' THEN 74 + (i % 12) WHEN 'P002' THEN 68 + (i % 9) ELSE 66 + (i % 10) END,
       CASE d.patient_id WHEN 'P002' THEN 94 + (i % 4)  ELSE 96 + (i % 4) END,
       36.4 + (i % 5) * 0.1,
       CASE d.patient_id WHEN 'P003' THEN 145 + (i % 8) ELSE 120 + (i % 10) END,
       CASE d.patient_id WHEN 'P003' THEN 92 + (i % 5)  ELSE 78 + (i % 6) END
FROM dossiers d, generate_series(0,39) AS i;

INSERT INTO alertes (patient_id, type, niveau, message, cree_le) VALUES
 ('P001','TACHYCARDIE',   'CRITIQUE','FC > 150 bpm pendant 3 mesures',  NOW() - INTERVAL '9 days'),
 ('P001','FIEVRE',        'MOYEN',   'Température > 39,5 °C',            NOW() - INTERVAL '3 days'),
 ('P002','DETRESSE_RESP', 'CRITIQUE','SpO2 < 90 %',                      NOW() - INTERVAL '6 days'),
 ('P005','CHUTE',         'CRITIQUE','Chute détectée par le capteur',    NOW() - INTERVAL '12 days');

INSERT INTO traitements (patient_id, medicament, posologie, debut, fin) VALUES
 ('P001','Bisoprolol 5 mg',    '1 comprimé le matin',  CURRENT_DATE - 200, NULL),
 ('P001','Furosémide 40 mg',   '1 comprimé le matin',  CURRENT_DATE - 200, NULL),
 ('P001','Ramipril 5 mg',      '1 comprimé le soir',   CURRENT_DATE - 200, NULL),
 ('P001','Amoxicilline 1 g',   '2 fois par jour',      CURRENT_DATE - 60,  CURRENT_DATE - 53),
 ('P002','Salbutamol spray',   '2 bouffées si besoin', CURRENT_DATE - 100, NULL),
 ('P003','Amlodipine 5 mg',    '1 comprimé par jour',  CURRENT_DATE - 150, NULL),
 ('P004','Metformine 850 mg',  '2 fois par jour',      CURRENT_DATE - 400, NULL),
 ('P005','Valproate 500 mg',   '2 fois par jour',      CURRENT_DATE - 300, NULL);

INSERT INTO soins (patient_id, date_soin, type, description, praticien) VALUES
 ('P001', CURRENT_DATE - 5,  'CONSULTATION','Contrôle insuffisance cardiaque, ajustement diurétique', 'Dr Nadia Khelifi'),
 ('P001', CURRENT_DATE - 12, 'EXAMEN',      'Échocardiographie : FEVG 40 %',                          'Dr Nadia Khelifi'),
 ('P001', CURRENT_DATE - 20, 'EXAMEN',      'Électrocardiogramme',                                    'Dr Nadia Khelifi'),
 ('P001', CURRENT_DATE - 28, 'ANALYSES',    'Bilan biologique : ionogramme, créatinine',              'Laboratoire central'),
 ('P001', CURRENT_DATE - 75, 'HOSPITALISATION','Décompensation cardiaque, 4 jours',                   'Dr Nadia Khelifi'),
 ('P002', CURRENT_DATE - 15, 'ANALYSES',    'Bilan biologique',                                       'Laboratoire central'),
 ('P002', CURRENT_DATE - 40, 'EXAMEN',      'Spirométrie',                                            'Dr Hichem Chaabane'),
 ('P003', CURRENT_DATE - 25, 'CONSULTATION','Suivi tension artérielle',                               'Dr Nadia Khelifi'),
 ('P004', CURRENT_DATE - 10, 'ANALYSES',    'HbA1c : 7,8 %',                                          'Laboratoire central'),
 ('P005', CURRENT_DATE - 12, 'URGENCE',     'Crise convulsive, prise en charge aux urgences',         'Dr Sami Rekik');
