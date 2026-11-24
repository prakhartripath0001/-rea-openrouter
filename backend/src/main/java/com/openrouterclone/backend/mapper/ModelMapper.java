package com.openrouterclone.backend.mapper;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Component;

import com.openrouterclone.backend.dto.ModelPricingResponse;
import com.openrouterclone.backend.dto.ModelProviderResponse;
import com.openrouterclone.backend.dto.ModelResponse;
import com.openrouterclone.backend.entity.ModelEntity;
import com.openrouterclone.backend.entity.ModelPricingEntity;
import com.openrouterclone.backend.entity.ModelProviderEntity;

@Component
public class ModelMapper {
    public ModelResponse toResponse(ModelEntity model) {
        List<ModelProviderResponse> providers = model.getModelProviders().stream()
                .sorted(Comparator.comparing(mp -> mp.getProvider().getName(), String.CASE_INSENSITIVE_ORDER))
                .map(this::toProviderResponse)
                .toList();

        return new ModelResponse(
                model.getId(),
                model.getModelKey(),
                model.getName(),
                model.getDescription(),
                model.getContextLength(),
                providers,
                model.getModalities().stream().map(row -> row.getModality()).sorted(String.CASE_INSENSITIVE_ORDER).toList(),
                model.getTags().stream().map(row -> row.getTag()).sorted(String.CASE_INSENSITIVE_ORDER).toList(),
                model.getSupportedParameters().stream().map(row -> row.getParameterName()).sorted().toList(),
                model.getCreatedAt(),
                model.getUpdatedAt());
    }

    private ModelProviderResponse toProviderResponse(ModelProviderEntity modelProvider) {
        List<ModelPricingResponse> pricing = modelProvider.getPricing().stream()
                .sorted(Comparator.comparing(ModelPricingEntity::getBillingUnit))
                .map(price -> new ModelPricingResponse(
                        price.getCurrency(), price.getBillingUnit(), price.getPromptRatePerMillion(),
                        price.getOutputRatePerMillion(), price.isFree(), price.isDiscounted()))
                .toList();
        return new ModelProviderResponse(
                modelProvider.getId(), modelProvider.getProvider().getSlug(), modelProvider.getProvider().getName(),
                modelProvider.getProviderModelKey(), pricing);
    }
}
