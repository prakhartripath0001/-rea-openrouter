# OpenRouter clone foundation

This is an independent model-catalogue application. The backend schema and seed data are designed for this project; they do not claim to represent OpenRouter's internal database or undocumented APIs.

## Repository layout

```text
docs/       Reverse-engineering reports and evidence notes
frontend/   React + TypeScript + Vite application
backend/    Java 21 + Spring Boot REST API and Flyway migrations
```

The model catalogue is seeded from clearly labeled fictional frontend fixtures. Seed prices are illustrative USD per million tokens. Replace them with verified, project-owned data when available.

## Prerequisites

- Java 21
- Maven 3.6.3 or newer
- Node.js and npm
- Docker Desktop or another working Docker Engine for PostgreSQL and database-backed integration tests

## Local setup

1. Copy `.env.example` to `.env` and set `DB_PASSWORD` to a local development password. The `.env` file is ignored by Git.
2. Start PostgreSQL:

   ```sh
   docker compose up -d postgres
   ```

3. Export the local environment for Spring Boot and start the API. Flyway runs migrations and demo seed data at application startup.

   ```sh
   set -a
   source .env
   set +a
   mvn -f backend/pom.xml spring-boot:run
   ```

4. In a second terminal, run the frontend:

   ```sh
   npm install
   npm run dev
   ```

Vite reads the root `.env` file. Set `VITE_API_BASE_URL` there to change the API base URL. The default is `http://localhost:8080/api/v1`.

## REST endpoints

- `GET /api/v1/models` — paged model results. Supports `q`, `modality`, repeated `providers` and `tags`, `minContextLength`, `maxContextLength`, `maxPromptPricePerMillion`, `maxOutputPricePerMillion`, `freeOnly`, `discountedOnly`, `page`, `size`, `sort`, and `direction`.
- `GET /api/v1/models/{uuid}` — one model and its provider/pricing options.
- `GET /api/v1/providers` — providers with model counts.
- `GET /api/v1/filters` — modalities, tags, supported parameters, providers, context, and price ranges.

For example:

```text
GET /api/v1/models?q=reasoner&modality=Text&providers=northstar&tags=Tools&page=0&size=20&sort=contextLength&direction=desc
```

Model IDs in routes are UUIDs. The original slash-style fixture identifier (such as `northstar/chat-2`) is exposed as `modelKey`.

## Build and tests

```sh
npm run build
mvn -f backend/pom.xml test
```

The backend unit tests run without Docker. The PostgreSQL integration suite uses Testcontainers; it is skipped when a Docker Engine is unavailable. CORS allows the default local Vite origins `http://localhost:5173` and `http://127.0.0.1:5173`; set `CORS_ALLOWED_ORIGINS` to configure them.

## Backend structure

```text
backend/src/main/java/com/openrouterclone/backend/
  config/       CORS and application configuration
  controller/   HTTP endpoints
  dto/          request and response contracts
  entity/       JPA entities
  exception/    consistent API error handling
  mapper/       entity-to-DTO mapping
  repository/   Spring Data repositories and query specifications
  service/      catalogue, provider, and filter logic
backend/src/main/resources/db/migration/  versioned Flyway SQL
backend/src/test/java/                   unit and PostgreSQL integration tests
```
