package com.openrouterclone.backend.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
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
}
