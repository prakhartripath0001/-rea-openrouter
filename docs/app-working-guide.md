# How the application works

This project is an independent model catalogue clone. Its database schema and six seeded model records are local demo data, not a description of OpenRouter's internal data model or current production catalogue.

## Runtime flow

```text
Browser
  └─ React page and reusable catalogue controls (Vite, port 5173)
       ├─ GET /api/v1/filters ─────────────┐
       └─ GET /api/v1/models?... ──────────┤
                                            v
                                  Spring MVC controllers (port 8080)
                                            v
                                  Services and DTO mappers
                                            v
                                  Spring Data JPA repositories
                                            v
                                  PostgreSQL (port 5432)
```

The Vite app reads `VITE_API_BASE_URL` from the root environment; the default is `http://localhost:8080/api/v1`. The frontend's `api/client.ts` performs GET requests, parses JSON, and turns non-2xx responses into errors using the server's safe `message`. `ModelsPage` loads filter options and the first model page, and uses a 250 ms delay before submitting changed search text. It converts API DTOs through `modelAdapter.ts` for display.

The backend controllers bind query parameters into validated request DTOs. Services apply paging, supported sort fields and query rules, repositories execute parameterized JPA Criteria specifications, and mappers assemble response DTOs from related entities. A shared exception handler maps validation errors to 400, absent models to 404, data constraint conflicts to 409, and unexpected failures to a generic 500 response.

## Data model

- `models` holds a UUID, unique slash-style `model_key`, display name, description, context length, and timestamps.
- `providers` holds unique provider slugs and names.
- `model_providers` links models to providers and stores provider-specific model keys.
- `model_pricing` stores currency, billing unit, prompt/output rates, and free/discounted flags for a model-provider entry.
- `model_modalities`, `model_tags`, and `supported_model_parameters` hold the model's repeated attributes.

Foreign keys enforce the relationships. Unique constraints prevent duplicate model-provider, model-modality, model-tag, parameter, and provider/model-key combinations. Check constraints reject invalid context lengths, negative prices, unsupported billing units, and malformed currency codes. Trigram and b-tree indexes support the implemented text and filter paths.

## Migrations and sample data

Flyway runs versioned SQL from `backend/src/main/resources/db/migration` when Spring starts. `V1` creates extensions, tables, constraints, and indexes. `V2` inserts fictional providers and models plus their modalities, prices, tags, and supported parameters. Hibernate uses `ddl-auto: validate`; it checks the mapped schema but does not create or mutate tables.

To reset the local database and replay migrations, stop the app and run `docker compose down -v`, then start PostgreSQL and Spring Boot again. This removes the local Docker volume and all data stored in it.

## API query behavior

`GET /api/v1/models` returns `{ content, page, size, totalElements, totalPages, first, last }`. `page` is zero-based, default page size is 24, and the maximum size is 100. Search checks model name, model key, description, and provider name/slug. Multiple provider filters are ORed; multiple tags are combined as required matches. Modality, context bounds, prompt/output price ceilings, free-only, and discounted-only conditions narrow results. Supported sort fields are name, context length, created time, and updated time; direction is `asc` or `desc`.

`GET /api/v1/models/{uuid}` returns one model with its providers, pricing, modalities, tags, and supported parameters. `GET /api/v1/providers` returns provider records and their model counts. `GET /api/v1/filters` returns the values/ranges available for the current seeded catalogue.

## Frontend scope and known UI limits

The current page includes debounced search, modality tabs, provider/tag/context/free/discount filters, list/table display, and sort selection. Its results request always asks for page 0 and size 50. There is no page-navigation control yet. The card's copy and detail icon buttons currently have no actions, and no frontend detail route is present; model details are available at the API endpoint only. Loading, error, retry, empty-results, and filter-options error states are implemented in the page source, but visual/browser interaction checks depend on an accessible browser session.

## Configuration

Copy `.env.example` to `.env` and set a local database password before starting Compose. Database connection settings and allowed local CORS origins are configurable through environment variables. Keep actual credentials in ignored `.env`, never in tracked files. Local CORS defaults to `http://localhost:5173` and `http://127.0.0.1:5173`.

## Verification notes

The backend suite covers unit behavior, controller binding/error responses, clean Flyway migration and seed counts, database indexes and CRUD, duplicate/check/FK constraint failures, and context/output-price filters. The current recorded run passed 28 tests. A production frontend build passed. Live HTTP smoke checks against the local Compose database passed for list, detail, providers, filters, filter combinations, sorting, pagination, invalid IDs/parameters, and CORS. Browser console inspection and click-through UI checks were not run because Chrome CDP on `127.0.0.1:9222` was unavailable in that session.
