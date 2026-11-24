package com.openrouterclone.backend.mapper;

import org.springframework.stereotype.Component;

import com.openrouterclone.backend.dto.ProviderResponse;
import com.openrouterclone.backend.entity.ProviderEntity;

@Component
public class ProviderMapper {
    public ProviderResponse toResponse(ProviderEntity provider, long modelCount) {
        return new ProviderResponse(provider.getId(), provider.getSlug(), provider.getName(),
                provider.getDescription(), modelCount);
    }
}
