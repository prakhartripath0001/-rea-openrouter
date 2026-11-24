CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE TABLE models (
    id UUID PRIMARY KEY,
    model_key VARCHAR(180) NOT NULL UNIQUE,
    name VARCHAR(180) NOT NULL,
    description TEXT NOT NULL,
    context_length INTEGER NOT NULL CHECK (context_length > 0),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE providers (
    id UUID PRIMARY KEY,
    slug VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(160) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE model_providers (
    id UUID PRIMARY KEY,
    model_id UUID NOT NULL REFERENCES models(id) ON DELETE CASCADE,
    provider_id UUID NOT NULL REFERENCES providers(id) ON DELETE RESTRICT,
    provider_model_key VARCHAR(180),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_model_provider UNIQUE (model_id, provider_id),
    CONSTRAINT uq_provider_model_key UNIQUE (provider_id, provider_model_key)
);

CREATE TABLE model_pricing (
    id UUID PRIMARY KEY,
    model_provider_id UUID NOT NULL REFERENCES model_providers(id) ON DELETE CASCADE,
    currency CHAR(3) NOT NULL DEFAULT 'USD',
    billing_unit VARCHAR(40) NOT NULL DEFAULT 'PER_MILLION_TOKENS',
    prompt_rate_per_million NUMERIC(18, 6) NOT NULL CHECK (prompt_rate_per_million >= 0),
    output_rate_per_million NUMERIC(18, 6) CHECK (output_rate_per_million >= 0),
    is_free BOOLEAN NOT NULL DEFAULT FALSE,
    is_discounted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_model_pricing_currency CHECK (currency ~ '^[A-Z]{3}$'),
    CONSTRAINT ck_model_pricing_billing_unit CHECK (billing_unit IN ('PER_MILLION_TOKENS', 'PER_MILLION_CHARACTERS', 'PER_SECOND', 'PER_REQUEST')),
    CONSTRAINT uq_model_provider_billing_unit UNIQUE (model_provider_id, billing_unit)
);

CREATE TABLE model_modalities (
    id UUID PRIMARY KEY,
    model_id UUID NOT NULL REFERENCES models(id) ON DELETE CASCADE,
    modality VARCHAR(40) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_model_modality UNIQUE (model_id, modality)
);

CREATE TABLE supported_model_parameters (
    id UUID PRIMARY KEY,
    model_id UUID NOT NULL REFERENCES models(id) ON DELETE CASCADE,
    parameter_name VARCHAR(100) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_model_parameter UNIQUE (model_id, parameter_name)
);

CREATE TABLE model_tags (
    id UUID PRIMARY KEY,
    model_id UUID NOT NULL REFERENCES models(id) ON DELETE CASCADE,
    tag VARCHAR(80) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_model_tag UNIQUE (model_id, tag)
);

CREATE INDEX idx_models_name_trgm ON models USING GIN (lower(name) gin_trgm_ops);
CREATE INDEX idx_models_key_trgm ON models USING GIN (lower(model_key) gin_trgm_ops);
CREATE INDEX idx_models_description_trgm ON models USING GIN (lower(description) gin_trgm_ops);
CREATE INDEX idx_models_context_length ON models (context_length);
CREATE INDEX idx_providers_name_trgm ON providers USING GIN (lower(name) gin_trgm_ops);
CREATE INDEX idx_providers_slug ON providers (slug);
CREATE INDEX idx_model_providers_provider_model ON model_providers (provider_id, model_id);
CREATE INDEX idx_model_pricing_prompt_rate ON model_pricing (prompt_rate_per_million);
CREATE INDEX idx_model_pricing_output_rate ON model_pricing (output_rate_per_million);
CREATE INDEX idx_model_pricing_flags ON model_pricing (is_free, is_discounted);
CREATE INDEX idx_model_modalities_modality ON model_modalities (modality, model_id);
CREATE INDEX idx_supported_parameters_name ON supported_model_parameters (parameter_name, model_id);
CREATE INDEX idx_model_tags_tag ON model_tags (tag, model_id);
