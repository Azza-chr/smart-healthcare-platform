\connect insurance_db

-- Assurés CNAM (système "legacy" certifié). patient_id = identifiant commun à toute la plateforme
CREATE TABLE assures (
    patient_id        VARCHAR(10) PRIMARY KEY,
    nom               VARCHAR(60) NOT NULL,
    prenom            VARCHAR(60) NOT NULL,
    date_naissance    DATE        NOT NULL,
    num_cnam          VARCHAR(20) UNIQUE NOT NULL,
    regime            VARCHAR(10) NOT NULL CHECK (regime IN ('CNSS','CNRPS')),
    statut_droits     VARCHAR(10) NOT NULL CHECK (statut_droits IN ('ACTIF','EXPIRE')),
    date_fin_droits   DATE        NOT NULL,
    taux_base         INT         NOT NULL   -- 80 ou 50 (%)
);

CREATE TABLE couvertures (
    id                 SERIAL PRIMARY KEY,
    patient_id         VARCHAR(10) NOT NULL REFERENCES assures(patient_id),
    type_prestation    VARCHAR(20) NOT NULL CHECK (type_prestation IN ('CONSULTATION','ANALYSES','HOSPITALISATION','TRANSPORT')),
    taux_remboursement INT         NOT NULL,
    plafond_annuel     NUMERIC(10,3) NOT NULL,
    UNIQUE (patient_id, type_prestation)
);

-- Nomenclature des actes (lettres-clés). Tarifs FICTIFS en dinars (TND)
CREATE TABLE actes_medicaux (
    code            VARCHAR(10) PRIMARY KEY,
    libelle         VARCHAR(100) NOT NULL,
    type_prestation VARCHAR(20)  NOT NULL,
    tarif           NUMERIC(10,3) NOT NULL
);

CREATE TABLE feuilles_soins (
    numero             VARCHAR(20) PRIMARY KEY,
    patient_id         VARCHAR(10) NOT NULL REFERENCES assures(patient_id),
    acte_code          VARCHAR(10) NOT NULL REFERENCES actes_medicaux(code),
    date_soin          DATE        NOT NULL,
    medecin            VARCHAR(80) NOT NULL,
    montant            NUMERIC(10,3) NOT NULL,
    taux_applique      INT         NOT NULL,
    montant_rembourse  NUMERIC(10,3) NOT NULL,
    statut             VARCHAR(12) NOT NULL DEFAULT 'CERTIFIEE',
    signature          VARCHAR(64) NOT NULL
);

INSERT INTO assures VALUES
 ('P001','Ben Salah','Ahmed',   '1958-03-14','CNAM-1000001','CNSS', 'ACTIF',  CURRENT_DATE + 365, 80),
 ('P002','Trabelsi', 'Sarra',   '1991-07-02','CNAM-1000002','CNSS', 'ACTIF',  CURRENT_DATE + 300, 80),
 ('P003','Gharbi',   'Mohamed', '1969-11-23','CNAM-1000003','CNRPS','ACTIF',  CURRENT_DATE + 200, 50),
 ('P004','Mansouri', 'Leila',   '1975-05-30','CNAM-1000004','CNSS', 'EXPIRE', CURRENT_DATE - 45,  80),
 ('P005','Jebali',   'Karim',   '1999-01-09','CNAM-1000005','CNSS', 'ACTIF',  CURRENT_DATE + 400, 80);

INSERT INTO couvertures (patient_id, type_prestation, taux_remboursement, plafond_annuel)
SELECT a.patient_id, t.type,
       CASE WHEN t.type = 'HOSPITALISATION' THEN a.taux_base + 10 ELSE a.taux_base END,
       CASE WHEN a.taux_base = 80 THEN 3000.000 ELSE 1500.000 END
FROM assures a
CROSS JOIN (VALUES ('CONSULTATION'),('ANALYSES'),('HOSPITALISATION'),('TRANSPORT')) AS t(type);

INSERT INTO actes_medicaux VALUES
 ('C',     'Consultation médecin généraliste',      'CONSULTATION',     25.000),
 ('CS',    'Consultation médecin spécialiste',      'CONSULTATION',     40.000),
 ('CSC',   'Consultation cardiologue',              'CONSULTATION',     50.000),
 ('URG',   'Consultation d''urgence',               'CONSULTATION',     60.000),
 ('ECG',   'Électrocardiogramme',                   'CONSULTATION',     30.000),
 ('SPIRO', 'Spirométrie',                           'CONSULTATION',     45.000),
 ('B100',  'Bilan biologique standard',             'ANALYSES',         70.000),
 ('HOSP1', 'Hospitalisation (par jour)',            'HOSPITALISATION', 180.000),
 ('AMB',   'Transport sanitaire (ambulance)',       'TRANSPORT',       120.000);

INSERT INTO feuilles_soins (numero, patient_id, acte_code, date_soin, medecin, montant, taux_applique, montant_rembourse, signature) VALUES
 ('FS-HIST-0001','P002','CS',    CURRENT_DATE - 40,'Dr Hichem Chaabane', 40.000,80, 32.000, md5('FS-HIST-0001P002')),
 ('FS-HIST-0002','P002','SPIRO', CURRENT_DATE - 40,'Dr Hichem Chaabane', 45.000,80, 36.000, md5('FS-HIST-0002P002')),
 ('FS-HIST-0003','P002','B100',  CURRENT_DATE - 15,'Dr Hichem Chaabane', 70.000,80, 56.000, md5('FS-HIST-0003P002')),
 ('FS-HIST-0004','P003','CSC',   CURRENT_DATE - 60,'Dr Nadia Khelifi',   50.000,50, 25.000, md5('FS-HIST-0004P003')),
 ('FS-HIST-0005','P003','ECG',   CURRENT_DATE - 60,'Dr Nadia Khelifi',   30.000,50, 15.000, md5('FS-HIST-0005P003')),
 ('FS-HIST-0006','P001','CSC',   CURRENT_DATE - 20,'Dr Nadia Khelifi',   50.000,80, 40.000, md5('FS-HIST-0006P001')),
 ('FS-HIST-0007','P001','ECG',   CURRENT_DATE - 20,'Dr Nadia Khelifi',   30.000,80, 24.000, md5('FS-HIST-0007P001'));
