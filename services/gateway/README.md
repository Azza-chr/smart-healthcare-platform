# gateway

Single entry point of the platform (Spring Cloud Gateway, reactive). Checks the JWT, applies the
role rules, handles CORS and routes to the services.

- **Port:** 8080
- **Health:** `GET /api/health` (combined status of all services), `GET /actuator/health`

## Run (development)

Start the database and the services, then run `GatewayApplication` with the environment variable
`JWT_SECRET` (same value as auth-service).

## Environment variables

| Variable | Default | Meaning |
|---|---|---|
| `JWT_SECRET` | none (required) | HS256 secret, at least 32 characters, shared with auth-service |
| `CORS_ORIGINS` | `http://localhost:3000,http://localhost:5173` | allowed web client origins |
| `AUTH_URL`, `REST_URL`, `SOAP_URL`, `GRAPHQL_URL`, `ORCHESTRATOR_URL` | `http://localhost:<port>` | service addresses |
| `TELEMETRY_HEALTH_URL` | `http://localhost:8084` | health port of the gRPC service |

## Routes and roles

| Path | Target | Roles |
|---|---|---|
| `POST /auth/login` | auth-service | public |
| `/api/rest/**` | REST appointments (prefix removed) | all; a patient only on `/patients/{own id}` |
| `POST /api/graphql` | GraphQL dashboard | MEDECIN, INFIRMIER, ADMIN |
| `/api/soap/**` | SOAP insurance (prefix removed) | MEDECIN, ADMIN |
| `/api/orchestrator/**` | orchestrator (prefix removed) | MEDECIN, ADMIN |

## Errors

`401` for a missing, invalid or expired token, `403` when the role is not allowed.
Body: `{status, message, timestamp}`. The Gateway removes client-sent `X-User-*` headers and sets
`X-User-Name`, `X-User-Role` and `X-Patient-Id` from the token for the services behind it.