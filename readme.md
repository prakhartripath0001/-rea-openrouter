# OpenRouter clone foundation

An independent model catalogue application with a React frontend and a Spring Boot/PostgreSQL API. The schema and seed records belong to this project; they do not claim to mirror OpenRouter's private implementation. Catalogue rows and prices are illustrative demo data.

## Repository layout

```text
docs/       Reverse-engineering notes and application working guide
frontend/   React + TypeScript + Vite UI
backend/    Java 21 + Spring Boot REST API, tests, and Flyway migrations
```

For the end-to-end request flow and data relationships, see [docs/app-working-guide.md](docs/app-working-guide.md).

## Prerequisites

- Java 21
- Maven 3.6.3 or newer
- Node.js and npm
- Docker Desktop or another Docker Engine (PostgreSQL and database integration tests)

## Run locally

From the repository root, set a local-only database password in the shell, then start PostgreSQL:

```sh
export DB_PASSWORD='choose-a-local-password'
docker compose up -d postgres
```

Wait until `docker compose ps` reports PostgreSQL as healthy. In terminal 1, start the API; Flyway applies the schema and demo-data migrations at startup:

```sh
mvn -f backend/pom.xml spring-boot:run
```

In terminal 2, install frontend dependencies and start Vite:

```sh
npm install
npm run dev
```

Open `http://localhost:5173`. Vite reads the root `.env` file. The API URL defaults to `http://localhost:8080/api/v1`; set `VITE_API_BASE_URL` in `.env` to override it. `DB_URL`, `DB_USER`, `DB_PASSWORD`, `DB_NAME`, `DB_PORT`, `SERVER_PORT`, and `CORS_ALLOWED_ORIGINS` are configurable as shown in [.env.example](.env.example). Never commit `.env` or real credentials.

Stop the app with Ctrl+C, then stop PostgreSQL with:

```sh
docker compose down
```

Use `docker compose down -v` only when you intentionally want to delete the local database volume and start from an empty database.

## API

- `GET /api/v1/models` — paged catalogue. Supports `q`, `modality`, repeated `providers` and `tags`, `minContextLength`, `maxContextLength`, `maxPromptPricePerMillion`, `maxOutputPricePerMillion`, `freeOnly`, `discountedOnly`, `page`, `size`, `sort`, and `direction`.
- `GET /api/v1/models/{uuid}` — model and provider/pricing details.
- `GET /api/v1/providers` — provider list and model counts.
- `GET /api/v1/filters` — available modalities, tags, parameters, providers, and context/price bounds.

Example:

```text
GET /api/v1/models?q=reasoner&modality=Text&providers=northstar&tags=Tools&page=0&size=20&sort=contextLength&direction=desc
```

Pages are zero-based and page size is limited to 100. Model route IDs are UUIDs; the slash-style fixture identifier (for example `northstar/chat-2`) is returned as `modelKey`.

## Tests and build

```sh
npm run build
mvn -f backend/pom.xml clean test
```

The Maven suite includes unit, web-layer, and PostgreSQL Testcontainers integration tests. Docker is required for the database integration tests; without Docker, Testcontainers is configured to skip that class. `npm run build` performs TypeScript project checks and a Vite production build.

## Verification record

Latest local verification (2026-11-24): `mvn -f backend/pom.xml clean test` passed 28 tests (0 failures/errors/skips), and `npm run build` passed. Against the local Compose database, Flyway V1 and V2 applied successfully; live smoke requests passed for the four routes, search, provider/modality/context/price/free filters, sorting, pagination, details, invalid IDs and input bounds, and configured CORS. The workspace's browser CDP endpoint was unavailable during this run, so interactive browser rendering, console, and click-based checks were not verified. See the working guide for current UI scope and verification limits.

## Backend structure

```text
backend/src/main/java/com/openrouterclone/backend/
  config/       CORS and app configuration
  controller/   HTTP routes
  dto/          request/response contracts
  entity/       JPA persistence model
  exception/    API error mapping
  mapper/       entity-to-response conversion
  repository/   Spring Data repositories and query specifications
  service/      catalogue, provider, and filter logic
backend/src/main/resources/db/migration/  versioned Flyway SQL
backend/src/test/java/                   unit and PostgreSQL integration tests
```
