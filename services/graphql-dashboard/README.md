# graphql-dashboard

GraphQL service for the medical dashboard: patient file in one request, dynamic filters,
on-demand care history, and creation of intervention sheets.

- **Port:** 8083 (endpoint `POST /graphql`, GraphiQL at `/graphiql`)
- **Database:** `dashboard_db` (owned by this service only)
- **Contract:** `contracts/graphql/schema.graphql`
- **Health:** `GET /actuator/health`

## Run (development)

1. Start the database from the repository root: `docker compose up -d`
2. In IntelliJ, run `GraphqlDashboardApplication` with the environment variable `DB_PASSWORD`
   set to the PostgreSQL password from your `.env`.

## Environment variables

| Variable | Default | Meaning |
|---|---|---|
| `DB_USER` | `admin` | database user |
| `DB_PASSWORD` | none (required) | database password |

## Operations

| Operation | Description |
|---|---|
| `patient(id)` | profile; vitals, alerts and treatments are loaded only if requested |
| `patients(service, pathologie, risque)` | dynamic filter, all arguments optional |
| `historiqueSoins(patientId, jours)` | care history with an aggregate by type |
| `creerFicheIntervention(input)` | creates an intervention sheet (used by the orchestrator) |

## Errors

Errors follow the GraphQL format and carry a classification: `NOT_FOUND` (unknown patient),
`BAD_REQUEST` (invalid input).