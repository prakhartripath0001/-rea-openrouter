package com.openrouterclone.backend.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.openrouterclone.backend.dto.FilterOptionsResponse;
import com.openrouterclone.backend.mapper.ProviderMapper;
import com.openrouterclone.backend.repository.ModelModalityRepository;
import com.openrouterclone.backend.repository.ModelPricingRepository;
import com.openrouterclone.backend.repository.ModelProviderRepository;
import com.openrouterclone.backend.repository.ModelRepository;
import com.openrouterclone.backend.repository.ModelTagRepository;
import com.openrouterclone.backend.repository.ProviderRepository;
import com.openrouterclone.backend.repository.SupportedModelParameterRepository;

@Service
@Transactional(readOnly = true)
public class FilterService {
    private final ModelRepository modelRepository;
    private final ModelModalityRepository modalityRepository;
    private final ModelTagRepository tagRepository;
    private final SupportedModelParameterRepository parameterRepository;
    private final ModelPricingRepository pricingRepository;
    private final ProviderRepository providerRepository;
    private final ModelProviderRepository modelProviderRepository;
    private final ProviderMapper providerMapper;

    public FilterService(ModelRepository modelRepository, ModelModalityRepository modalityRepository,
            ModelTagRepository tagRepository, SupportedModelParameterRepository parameterRepository,
            ModelPricingRepository pricingRepository, ProviderRepository providerRepository,
            ModelProviderRepository modelProviderRepository, ProviderMapper providerMapper) {
        this.modelRepository = modelRepository;
        this.modalityRepository = modalityRepository;
        this.tagRepository = tagRepository;
        this.parameterRepository = parameterRepository;
        this.pricingRepository = pricingRepository;
        this.providerRepository = providerRepository;
        this.modelProviderRepository = modelProviderRepository;
        this.providerMapper = providerMapper;
    }

    public FilterOptionsResponse getOptions() {
        List<String> modalities = modalityRepository.findAllByOrderByModalityAsc().stream()
                .map(row -> row.getModality()).distinct().toList();
        List<String> tags = tagRepository.findAllByOrderByTagAsc().stream()
                .map(row -> row.getTag()).distinct().toList();
        List<String> supportedParameters = parameterRepository.findAllByOrderByParameterNameAsc().stream()
                .map(row -> row.getParameterName()).distinct().toList();
        var providers = providerRepository.findAll(Sort.by(Sort.Direction.ASC, "name")).stream()
                .map(provider -> providerMapper.toResponse(provider,
                        modelProviderRepository.countByProvider_Id(provider.getId())))
                .toList();

        return new FilterOptionsResponse(modalities, providers, tags, supportedParameters,
                modelRepository.findMinimumContextLength(), modelRepository.findMaximumContextLength(),
                pricingRepository.findMinimumPromptRate(), pricingRepository.findMaximumPromptRate(),
                pricingRepository.findMinimumOutputRate(), pricingRepository.findMaximumOutputRate());
    }
}
