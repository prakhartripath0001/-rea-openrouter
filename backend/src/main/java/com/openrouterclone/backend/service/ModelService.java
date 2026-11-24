package com.openrouterclone.backend.service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.openrouterclone.backend.dto.ModelResponse;
import com.openrouterclone.backend.dto.ModelSearchRequest;
import com.openrouterclone.backend.dto.PageResponse;
import com.openrouterclone.backend.entity.ModelEntity;
import com.openrouterclone.backend.mapper.ModelMapper;
import com.openrouterclone.backend.repository.ModelRepository;
import com.openrouterclone.backend.repository.specification.ModelSpecifications;

@Service
@Transactional(readOnly = true)
public class ModelService {
    private static final int DEFAULT_PAGE_SIZE = 24;
    private static final int MAX_PAGE_SIZE = 100;
    private static final Set<String> ALLOWED_SORTS = Set.of("name", "contextLength", "createdAt", "updatedAt");

    private final ModelRepository modelRepository;
    private final ModelMapper modelMapper;

    public ModelService(ModelRepository modelRepository, ModelMapper modelMapper) {
        this.modelRepository = modelRepository;
        this.modelMapper = modelMapper;
    }

    public PageResponse<ModelResponse> list(ModelSearchRequest request) {
        int page = request.page() == null ? 0 : request.page();
        int size = request.size() == null ? DEFAULT_PAGE_SIZE : request.size();
        if (size > MAX_PAGE_SIZE) throw new InvalidModelQueryException("size must not exceed " + MAX_PAGE_SIZE);
        if (request.minContextLength() != null && request.maxContextLength() != null
                && request.minContextLength() > request.maxContextLength()) {
            throw new InvalidModelQueryException("minContextLength must not exceed maxContextLength");
        }

        String sortField = request.sort() == null || request.sort().isBlank() ? "name" : request.sort();
        if (!ALLOWED_SORTS.contains(sortField)) {
            throw new InvalidModelQueryException("sort must be one of: " + String.join(", ", ALLOWED_SORTS));
        }
        Sort.Direction direction;
        try {
            direction = Sort.Direction.fromString(request.direction() == null ? "asc" : request.direction());
        } catch (IllegalArgumentException exception) {
            throw new InvalidModelQueryException("direction must be 'asc' or 'desc'");
        }

        Specification<ModelEntity> specification = Specification.where(ModelSpecifications.search(request.q()))
                .and(ModelSpecifications.modality(request.modality()))
                .and(ModelSpecifications.providers(request.providers()))
                .and(ModelSpecifications.minContextLength(request.minContextLength()))
                .and(ModelSpecifications.maxContextLength(request.maxContextLength()))
                .and(ModelSpecifications.pricing(request.maxPromptPricePerMillion(), request.maxOutputPricePerMillion(),
                        request.freeOnly(), request.discountedOnly()));
        if (request.tags() != null) {
            for (String tag : request.tags()) specification = specification.and(ModelSpecifications.hasTag(tag));
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        Page<ModelResponse> responsePage = modelRepository.findAll(specification, pageable).map(modelMapper::toResponse);
        return PageResponse.from(responsePage);
    }

    public ModelResponse get(UUID id) {
        ModelEntity model = modelRepository.findById(id).orElseThrow(() -> new ModelNotFoundException(id));
        return modelMapper.toResponse(model);
    }
}
