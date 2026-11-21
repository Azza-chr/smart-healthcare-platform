\connect orchestrator_db

-- Journal des workflows : un enregistrement par alerte traitée
CREATE TABLE workflow_executions (
    id             UUID PRIMARY KEY,
    alerte_id      VARCHAR(40) NOT NULL,
    patient_id     VARCHAR(10) NOT NULL,
    type_alerte    VARCHAR(30),
    statut         VARCHAR(20) NOT NULL CHECK (statut IN ('EN_COURS','TERMINE','DEGRADE','ECHEC')),
    mode_degrade   BOOLEAN     NOT NULL DEFAULT FALSE,
    demarre_le     TIMESTAMP   NOT NULL DEFAULT NOW(),
    termine_le     TIMESTAMP
);
-- Idempotence : une seule exécution par alerte
CREATE UNIQUE INDEX uq_workflow_alerte ON workflow_executions (alerte_id);

-- Une ligne par étape (gRPC, SOAP, REST, GraphQL)
CREATE TABLE workflow_steps (
    id            BIGSERIAL PRIMARY KEY,
    execution_id  UUID        NOT NULL REFERENCES workflow_executions(id) ON DELETE CASCADE,
    ordre         INT         NOT NULL,
    etape         VARCHAR(30) NOT NULL,
    protocole     VARCHAR(10) NOT NULL,
    statut        VARCHAR(10) NOT NULL CHECK (statut IN ('OK','ECHEC','IGNORE','DEGRADE')),
    detail        VARCHAR(300),
    duree_ms      INT
);

-- Ambulances simulées
CREATE TABLE interventions (
    reference     VARCHAR(20) PRIMARY KEY,          -- AMB-xxxx
    execution_id  UUID        REFERENCES workflow_executions(id),
    patient_id    VARCHAR(10) NOT NULL,
    statut        VARCHAR(15) NOT NULL DEFAULT 'DECLENCHEE',
    cree_le       TIMESTAMP   NOT NULL DEFAULT NOW()
);
