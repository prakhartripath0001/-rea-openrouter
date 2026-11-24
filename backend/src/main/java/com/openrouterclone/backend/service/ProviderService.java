package com.openrouterclone.backend.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.openrouterclone.backend.dto.ProviderResponse;
import com.openrouterclone.backend.mapper.ProviderMapper;
import com.openrouterclone.backend.repository.ModelProviderRepository;
import com.openrouterclone.backend.repository.ProviderRepository;

@Service
@Transactional(readOnly = true)
public class ProviderService {
    private final ProviderRepository providerRepository;
    private final ModelProviderRepository modelProviderRepository;
    private final ProviderMapper providerMapper;

    public ProviderService(ProviderRepository providerRepository, ModelProviderRepository modelProviderRepository,
            ProviderMapper providerMapper) {
        this.providerRepository = providerRepository;
        this.modelProviderRepository = modelProviderRepository;
        this.providerMapper = providerMapper;
    }

    public List<ProviderResponse> list() {
        return providerRepository.findAll(Sort.by(Sort.Direction.ASC, "name")).stream()
                .map(provider -> providerMapper.toResponse(provider,
                        modelProviderRepository.countByProvider_Id(provider.getId())))
                .toList();
    }
}
