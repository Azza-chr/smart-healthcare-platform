# auth-service

Login service: checks username and password (BCrypt) and issues a signed JWT with the user's role.

- **Port:** 8086 (`POST /auth/login`)
- **Database:** `auth_db` (table `utilisateurs`, owned by this service only)
- **Health:** `GET /actuator/health`

## Run (development)

1. Start the database from the repository root: `docker compose up -d`
2. In IntelliJ, run `AuthServiceApplication` with the environment variables `DB_PASSWORD` and `JWT_SECRET`.

## Environment variables

| Variable | Default | Meaning |
|---|---|---|
| `DB_USER` | `admin` | database user |
| `DB_PASSWORD` | none (required) | database password |
| `JWT_SECRET` | none (required) | HS256 signing secret, at least 32 characters, shared with the Gateway |

## Login

`POST /auth/login` with `{"username": "...", "password": "..."}` returns
`{token, tokenType, expiresIn, username, nomComplet, role, patientId}`.

Token claims: `sub` (username), `role` (PATIENT, MEDECIN, INFIRMIER, ADMIN), `nom`,
`patientId` (patients only), `iat`, `exp` (1 hour).

## Errors

`401` for a wrong username, wrong password or disabled account (same message for all three),
`400` for a missing field or malformed body. Body: `{status, message, timestamp}`.