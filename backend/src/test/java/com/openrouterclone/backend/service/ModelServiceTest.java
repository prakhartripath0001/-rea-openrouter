package com.openrouterclone.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.openrouterclone.backend.dto.ModelResponse;
import com.openrouterclone.backend.dto.ModelSearchRequest;
import com.openrouterclone.backend.entity.ModelEntity;
import com.openrouterclone.backend.mapper.ModelMapper;
import com.openrouterclone.backend.repository.ModelRepository;

@ExtendWith(MockitoExtension.class)
class ModelServiceTest {
    @Mock private ModelRepository modelRepository;
    @Mock private ModelMapper modelMapper;

    private ModelService modelService;

    @BeforeEach
    void setUp() {
        modelService = new ModelService(modelRepository, modelMapper);
    }

    @Test
    void listUsesDefaultsAndReturnsPagedResult() {
        when(modelRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(Page.empty());

        var response = modelService.list(new ModelSearchRequest(null, null, null, null, null, null,
                null, null, null, null, null, null, null, null));

        assertEquals(0, response.page());
        assertEquals(24, response.size());
        assertEquals(0, response.totalElements());
        verify(modelRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void rejectsUnsupportedSortField() {
        ModelSearchRequest request = new ModelSearchRequest(null, null, null, null, null, null,
                null, null, null, null, 0, 20, "promptPrice", "asc");

        assertThrows(InvalidModelQueryException.class, () -> modelService.list(request));
        verify(modelRepository, never()).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void rejectsContextRangeWhenMinimumExceedsMaximum() {
        ModelSearchRequest request = new ModelSearchRequest(null, null, null, null, 100_000, 32_000,
                null, null, null, null, 0, 20, "name", "asc");

        assertThrows(InvalidModelQueryException.class, () -> modelService.list(request));
    }

    @Test
    void returnsDetailsForKnownId() {
        UUID id = UUID.randomUUID();
        ModelEntity entity = org.mockito.Mockito.mock(ModelEntity.class);
        ModelResponse expected = org.mockito.Mockito.mock(ModelResponse.class);
        when(modelRepository.findById(id)).thenReturn(Optional.of(entity));
        when(modelMapper.toResponse(entity)).thenReturn(expected);

        assertSame(expected, modelService.get(id));
    }

    @Test
    void throwsNotFoundForUnknownId() {
        UUID id = UUID.randomUUID();
        when(modelRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ModelNotFoundException.class, () -> modelService.get(id));
    }
}
