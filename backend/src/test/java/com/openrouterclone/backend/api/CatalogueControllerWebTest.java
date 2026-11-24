package com.openrouterclone.backend.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.openrouterclone.backend.config.CorsConfig;
import com.openrouterclone.backend.dto.FilterOptionsResponse;
import com.openrouterclone.backend.dto.ModelResponse;
import com.openrouterclone.backend.dto.ModelSearchRequest;
import com.openrouterclone.backend.dto.PageResponse;
import com.openrouterclone.backend.dto.ProviderResponse;
import com.openrouterclone.backend.exception.GlobalExceptionHandler;
import com.openrouterclone.backend.service.FilterService;
import com.openrouterclone.backend.service.InvalidModelQueryException;
import com.openrouterclone.backend.service.ModelNotFoundException;
import com.openrouterclone.backend.service.ModelService;
import com.openrouterclone.backend.service.ProviderService;

@WebMvcTest
@Import({GlobalExceptionHandler.class, CorsConfig.class})
class CatalogueControllerWebTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ModelService modelService;

    @MockitoBean
    private ProviderService providerService;

    @MockitoBean
    private FilterService filterService;

    @Test
    void modelListBindsSearchFiltersSortAndPagination() throws Exception {
        when(modelService.list(any())).thenReturn(emptyPage());

        mockMvc.perform(get("/api/v1/models")
                        .param("q", "Vision")
                        .param("modality", "Image")
                        .param("providers", "aster", "northstar")
                        .param("tags", "Tools")
                        .param("minContextLength", "32000")
                        .param("maxContextLength", "160000")
                        .param("maxPromptPricePerMillion", "0.500000")
                        .param("freeOnly", "false")
                        .param("discountedOnly", "true")
                        .param("page", "2")
                        .param("size", "10")
                        .param("sort", "contextLength")
                        .param("direction", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(24))
                .andExpect(jsonPath("$.content").isArray());

        org.mockito.ArgumentCaptor<ModelSearchRequest> request = org.mockito.ArgumentCaptor.forClass(ModelSearchRequest.class);
        verify(modelService).list(request.capture());
        org.assertj.core.api.Assertions.assertThat(request.getValue().q()).isEqualTo("Vision");
        org.assertj.core.api.Assertions.assertThat(request.getValue().providers()).containsExactly("aster", "northstar");
        org.assertj.core.api.Assertions.assertThat(request.getValue().tags()).containsExactly("Tools");
        org.assertj.core.api.Assertions.assertThat(request.getValue().page()).isEqualTo(2);
        org.assertj.core.api.Assertions.assertThat(request.getValue().direction()).isEqualTo("desc");
    }

    @Test
    void missingOptionalSearchParametersUseServiceDefaults() throws Exception {
        when(modelService.list(any())).thenReturn(emptyPage());

        mockMvc.perform(get("/api/v1/models"))
                .andExpect(status().isOk());

        org.mockito.ArgumentCaptor<ModelSearchRequest> request = org.mockito.ArgumentCaptor.forClass(ModelSearchRequest.class);
        verify(modelService).list(request.capture());
        org.assertj.core.api.Assertions.assertThat(request.getValue().q()).isNull();
        org.assertj.core.api.Assertions.assertThat(request.getValue().page()).isNull();
        org.assertj.core.api.Assertions.assertThat(request.getValue().size()).isNull();
    }

    @Test
    void validatesPagePriceAndSearchLengthInputs() throws Exception {
        when(modelService.list(any())).thenAnswer(invocation -> {
            ModelSearchRequest request = invocation.getArgument(0);
            if (request.minContextLength() != null && request.maxContextLength() != null
                    && request.minContextLength() > request.maxContextLength()) {
                throw new InvalidModelQueryException("minContextLength must not exceed maxContextLength");
            }
            return emptyPage();
        });

        mockMvc.perform(get("/api/v1/models").param("page", "-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
        mockMvc.perform(get("/api/v1/models").param("size", "101"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/api/v1/models").param("maxPromptPricePerMillion", "-0.1"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/api/v1/models").param("q", "x".repeat(201)))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/api/v1/models").param("minContextLength", "100000").param("maxContextLength", "32000"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("minContextLength must not exceed maxContextLength"));
    }

    @Test
    void mapsInvalidSortAndDirectionTo400() throws Exception {
        when(modelService.list(any())).thenThrow(new InvalidModelQueryException("invalid query"));

        mockMvc.perform(get("/api/v1/models").param("sort", "internalField"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    @Test
    void detailsProvidersAndFiltersExposeExpectedJsonContracts() throws Exception {
        UUID id = UUID.randomUUID();
        when(modelService.get(id)).thenReturn(new ModelResponse(id, "aster/vision-pro", "Vision Pro", "Demo",
                96_000, List.of(), List.of("Image", "Text"), List.of("Vision"), List.of(), Instant.now(), Instant.now()));
        when(providerService.list()).thenReturn(List.of(new ProviderResponse(UUID.randomUUID(), "aster", "Aster", null, 1)));
        when(filterService.getOptions()).thenReturn(new FilterOptionsResponse(List.of("Image", "Text"), List.of(),
                List.of("Vision"), List.of("tools"), 32000, 96000, BigDecimal.ZERO, BigDecimal.ONE, BigDecimal.ZERO, BigDecimal.TEN));

        mockMvc.perform(get("/api/v1/models/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modelKey").value("aster/vision-pro"))
                .andExpect(jsonPath("$.contextLength").value(96000));
        mockMvc.perform(get("/api/v1/providers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].slug").value("aster"));
        mockMvc.perform(get("/api/v1/filters"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modalities[0]").value("Image"))
                .andExpect(jsonPath("$.tags[0]").value("Vision"));
    }

    @Test
    void mapsUnknownAndMalformedModelIdsTo404And400() throws Exception {
        UUID id = UUID.randomUUID();
        when(modelService.get(id)).thenThrow(new ModelNotFoundException(id));

        mockMvc.perform(get("/api/v1/models/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
        mockMvc.perform(get("/api/v1/models/not-a-uuid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void corsAllowsConfiguredLocalFrontendOrigin() throws Exception {
        mockMvc.perform(get("/api/v1/models").header("Origin", "http://localhost:5173"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
    }

    private PageResponse<ModelResponse> emptyPage() {
        return new PageResponse<>(List.of(), 0, 24, 0, 0, true, true);
    }
}
