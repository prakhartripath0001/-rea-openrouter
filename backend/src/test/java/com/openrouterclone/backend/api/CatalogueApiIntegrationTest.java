package com.openrouterclone.backend.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import java.net.URI;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.JsonNode;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CatalogueApiIntegrationTest {
    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("catalogue_test")
            .withUsername("catalogue_test")
            .withPassword("catalogue_test");

    @DynamicPropertySource
    static void configureDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @LocalServerPort
    private int serverPort;

    @Test
    void cleanMigrationCreatesExpectedSchemaIndexesAndSeedRows() {
        assertThat(jdbcTemplate.queryForObject("select count(*) from flyway_schema_history where success", Integer.class))
                .isGreaterThanOrEqualTo(2);
        assertThat(jdbcTemplate.queryForObject("select count(*) from models", Integer.class)).isEqualTo(6);
        assertThat(jdbcTemplate.queryForObject("select count(*) from providers", Integer.class)).isEqualTo(5);
        assertThat(jdbcTemplate.queryForObject("select count(*) from model_modalities", Integer.class)).isEqualTo(11);
        assertThat(jdbcTemplate.queryForObject("select count(*) from model_pricing", Integer.class)).isEqualTo(6);
        assertThat(jdbcTemplate.queryForObject("select count(*) from supported_model_parameters", Integer.class)).isEqualTo(4);
        assertThat(jdbcTemplate.queryForObject("select count(*) from model_tags", Integer.class)).isEqualTo(10);
        assertThat(jdbcTemplate.queryForObject("select count(*) from pg_indexes where schemaname = current_schema() and indexname = 'idx_models_name_trgm'", Integer.class))
                .isEqualTo(1);
    }

    @Test
    void listsSeededModelsAndSupportsNameSearchAndPagination() {
        ResponseEntity<JsonNode> response = restTemplate.getForEntity("/api/v1/models?q=Vision&page=0&size=1", JsonNode.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().path("content")).hasSize(1);
        assertThat(response.getBody().path("content").get(0).path("modelKey").asText()).isEqualTo("aster/vision-pro");
        assertThat(response.getBody().path("totalElements").asLong()).isEqualTo(1);
        assertThat(response.getBody().path("size").asInt()).isEqualTo(1);
    }

    @Test
    void searchesProviderAndTreatsLikeWildcardsLiterally() {
        ResponseEntity<JsonNode> providerSearch = restTemplate.getForEntity("/api/v1/models?q=Northstar", JsonNode.class);
        URI wildcardSearchUri = URI.create("http://localhost:" + serverPort + "/api/v1/models?q=%25");
        ResponseEntity<JsonNode> wildcardSearch = restTemplate.getForEntity(wildcardSearchUri, JsonNode.class);

        assertThat(providerSearch.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(providerSearch.getBody().path("totalElements").asLong()).isEqualTo(2);
        assertThat(wildcardSearch.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(wildcardSearch.getBody().path("totalElements").asLong()).isZero();
    }

    @Test
    void supportsModalitySortingAndPageMetadata() {
        ResponseEntity<JsonNode> modality = restTemplate.getForEntity("/api/v1/models?modality=Video", JsonNode.class);
        ResponseEntity<JsonNode> paged = restTemplate.getForEntity(
                "/api/v1/models?page=1&size=2&sort=name&direction=desc", JsonNode.class);

        assertThat(modality.getBody().path("totalElements").asLong()).isEqualTo(1);
        assertThat(modality.getBody().path("content").get(0).path("modelKey").asText()).isEqualTo("lumen/video-understand");
        assertThat(paged.getBody().path("totalElements").asLong()).isEqualTo(6);
        assertThat(paged.getBody().path("totalPages").asInt()).isEqualTo(3);
        assertThat(paged.getBody().path("content")).hasSize(2);
        assertThat(paged.getBody().path("content").get(0).path("name").asText()).isEqualTo("Speech Live");
    }

    @Test
    void supportsMultipleProviderFiltersAsAnOrGroup() {
        ResponseEntity<JsonNode> response = restTemplate.getForEntity(
                "/api/v1/models?providers=northstar&providers=lumen", JsonNode.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().path("totalElements").asLong()).isEqualTo(3);
    }

    @Test
    void filtersByProviderModalityTagAndPricing() {
        ResponseEntity<JsonNode> response = restTemplate.getForEntity(
                "/api/v1/models?providers=northstar&modality=Text&tags=Tools&size=10", JsonNode.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().path("totalElements").asLong()).isEqualTo(2);

        ResponseEntity<JsonNode> discounted = restTemplate.getForEntity(
                "/api/v1/models?discountedOnly=true&maxPromptPricePerMillion=0.50", JsonNode.class);
        assertThat(discounted.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(discounted.getBody().path("totalElements").asLong()).isEqualTo(1);
        assertThat(discounted.getBody().path("content").get(0).path("modelKey").asText()).isEqualTo("aster/vision-pro");
    }

    @Test
    void filtersByContextRangeAndMaximumOutputPrice() {
        ResponseEntity<JsonNode> context = restTemplate.getForEntity(
                "/api/v1/models?minContextLength=96000&maxContextLength=128000", JsonNode.class);
        ResponseEntity<JsonNode> outputPrice = restTemplate.getForEntity(
                "/api/v1/models?maxOutputPricePerMillion=1.00", JsonNode.class);

        assertThat(context.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(context.getBody().path("totalElements").asLong()).isEqualTo(2);
        assertThat(outputPrice.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(outputPrice.getBody().path("totalElements").asLong()).isEqualTo(1);
        assertThat(outputPrice.getBody().path("content").get(0).path("modelKey").asText()).isEqualTo("northstar/chat-2");
    }

    @Test
    void listsProvidersAndFilterOptions() {
        ResponseEntity<JsonNode> providers = restTemplate.getForEntity("/api/v1/providers", JsonNode.class);
        ResponseEntity<JsonNode> filters = restTemplate.getForEntity("/api/v1/filters", JsonNode.class);

        assertThat(providers.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(providers.getBody()).hasSize(5);
        assertThat(filters.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(filters.getBody().path("modalities").toString()).contains("Text", "Image", "Audio", "Video");
        assertThat(filters.getBody().path("maxContextLength").asInt()).isEqualTo(160_000);
    }

    @Test
    void servesModelDetailsAndReturns404ForUnknownId() {
        JsonNode listBody = restTemplate.getForObject("/api/v1/models?size=1", JsonNode.class);
        String id = listBody.path("content").get(0).path("id").asText();

        ResponseEntity<JsonNode> detail = restTemplate.getForEntity("/api/v1/models/{id}", JsonNode.class, id);
        ResponseEntity<JsonNode> missing = restTemplate.getForEntity(
                "/api/v1/models/00000000-0000-4000-8000-000000000099", JsonNode.class);

        assertThat(detail.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(detail.getBody().path("id").asText()).isEqualTo(id);
        assertThat(detail.getBody().path("providers").get(0).path("pricing").get(0).path("billingUnit").asText())
                .isEqualTo("PER_MILLION_TOKENS");
        assertThat(missing.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void allowsConfiguredFrontendOrigin() {
        HttpHeaders headers = new HttpHeaders();
        headers.setOrigin("http://localhost:5173");
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/models", HttpMethod.GET, new HttpEntity<>(headers), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getAccessControlAllowOrigin()).isEqualTo("http://localhost:5173");
    }

    @Test
    void databaseSupportsCreateReadUpdateDelete() {
        UUID id = UUID.randomUUID();
        String modelKey = "test/temporary-model";
        jdbcTemplate.update("insert into models (id, model_key, name, description, context_length) values (?, ?, ?, ?, ?)",
                id, modelKey, "Temporary", "Test row", 4096);
        assertThat(jdbcTemplate.queryForObject("select name from models where id = ?", String.class, id)).isEqualTo("Temporary");

        jdbcTemplate.update("update models set name = ? where id = ?", "Updated temporary", id);
        assertThat(jdbcTemplate.queryForObject("select name from models where id = ?", String.class, id)).isEqualTo("Updated temporary");

        jdbcTemplate.update("delete from models where id = ?", id);
        assertThat(jdbcTemplate.queryForObject("select count(*) from models where id = ?", Integer.class, id)).isZero();
    }

    @Test
    void databaseRejectsDuplicateKeysInvalidRowsAndBrokenForeignKeys() {
        assertThatThrownBy(() -> jdbcTemplate.update(
                "insert into providers (id, slug, name) values (?, ?, ?)", UUID.randomUUID(), "northstar", "Duplicate"))
                .isInstanceOf(DataIntegrityViolationException.class);

        assertThatThrownBy(() -> jdbcTemplate.update(
                "insert into models (id, model_key, name, description, context_length) values (?, ?, ?, ?, ?)",
                UUID.randomUUID(), "test/invalid-context", "Invalid", "Invalid context", -1))
                .isInstanceOf(DataIntegrityViolationException.class);

        UUID providerId = jdbcTemplate.queryForObject("select id from providers limit 1", UUID.class);
        assertThatThrownBy(() -> jdbcTemplate.update(
                "insert into model_providers (id, model_id, provider_id) values (?, ?, ?)",
                UUID.randomUUID(), UUID.randomUUID(), providerId))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
