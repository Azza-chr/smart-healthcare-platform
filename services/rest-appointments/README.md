# rest-appointments

REST service for the patient portal: free slots, appointments (CRUD), emergency booking,
prescriptions and patient profile.

- **Port:** 8081
- **Database:** `appointments_db` (PostgreSQL, owned by this service only)
- **Contract:** `contracts/rest/openapi.yaml`
- **Health:** `GET /actuator/health`

## Run (development)

1. Start the database from the repository root: `docker compose up -d`
2. In IntelliJ, run `RestAppointmentsApplication` with the environment variable `DB_PASSWORD`
   set to the PostgreSQL password from your `.env`.

## Environment variables

| Variable | Default | Meaning |
|---|---|---|
| `DB_USER` | `admin` | database user |
| `DB_PASSWORD` | none (required) | database password |

## Endpoints

| Method | Path | Description |
|---|---|---|
| GET | `/creneaux?specialite=&date=` | free slots |
| POST | `/rendez-vous` | book a slot (409 if taken) |
| POST | `/rendez-vous/urgence` | book the first free emergency slot |
| GET / PUT / DELETE | `/rendez-vous/{id}` | read, move, cancel |
| GET / PUT | `/patients/{id}` | read, update contact details |
| GET | `/patients/{id}/ordonnances` | prescriptions, newest first |

## Errors

All errors share one body: `{ "timestamp", "status", "error", "message" }`.
Codes used: 400 (validation), 404 (unknown resource), 409 (conflict).

## Rules

- A slot can have only one confirmed appointment (row lock plus a unique index in the database).
- Emergency slots can only be booked through `/rendez-vous/urgence`.
- Emergency appointments cannot be modified.