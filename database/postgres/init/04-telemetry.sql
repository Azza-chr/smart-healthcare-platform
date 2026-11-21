\connect telemetry_db

CREATE TABLE capteurs (
    id         VARCHAR(20) PRIMARY KEY,
    patient_id VARCHAR(10) NOT NULL,
    type       VARCHAR(20) NOT NULL,
    actif      BOOLEAN     NOT NULL DEFAULT TRUE
);

-- Règles métier de détection (modifiables sans recompiler le service)
CREATE TABLE seuils (
    id                      SERIAL PRIMARY KEY,
    constante               VARCHAR(20) NOT NULL,   -- FC, SPO2, TEMPERATURE, CHUTE
    operateur               VARCHAR(2)  NOT NULL CHECK (operateur IN ('>','<','=')),
    valeur                  NUMERIC(6,1) NOT NULL,
    niveau                  VARCHAR(10) NOT NULL CHECK (niveau IN ('MOYEN','CRITIQUE')),
    mesures_consecutives    INT         NOT NULL DEFAULT 1,  -- règle anti faux-positif
    type_alerte             VARCHAR(30) NOT NULL
);

CREATE TABLE mesures (
    id          BIGSERIAL PRIMARY KEY,
    capteur_id  VARCHAR(20) NOT NULL REFERENCES capteurs(id),
    patient_id  VARCHAR(10) NOT NULL,
    mesure_le   TIMESTAMP   NOT NULL DEFAULT NOW(),
    frequence_cardiaque INT,
    spo2        INT,
    temperature NUMERIC(4,1),
    chute       BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE INDEX idx_mesures_patient ON mesures (patient_id, mesure_le DESC);

CREATE TABLE alertes (
    id          BIGSERIAL PRIMARY KEY,
    patient_id  VARCHAR(10) NOT NULL,
    type        VARCHAR(30) NOT NULL,
    niveau      VARCHAR(10) NOT NULL,
    valeur      NUMERIC(6,1),
    message     VARCHAR(200),
    cree_le     TIMESTAMP   NOT NULL DEFAULT NOW(),
    traitee     BOOLEAN     NOT NULL DEFAULT FALSE
);

INSERT INTO capteurs VALUES
 ('CAP-P001','P001','CARDIO'),
 ('CAP-P002','P002','RESPIRATOIRE'),
 ('CAP-P003','P003','CARDIO'),
 ('CAP-P004','P004','GLYCEMIE'),
 ('CAP-P005','P005','CHUTE');

INSERT INTO seuils (constante, operateur, valeur, niveau, mesures_consecutives, type_alerte) VALUES
 ('FC',          '>', 150.0, 'CRITIQUE', 3, 'TACHYCARDIE'),
 ('FC',          '<',  40.0, 'CRITIQUE', 3, 'BRADYCARDIE'),
 ('SPO2',        '<',  90.0, 'CRITIQUE', 2, 'DETRESSE_RESPIRATOIRE'),
 ('TEMPERATURE', '>',  39.5, 'MOYEN',    3, 'FIEVRE'),
 ('CHUTE',       '=',   1.0, 'CRITIQUE', 1, 'CHUTE');
