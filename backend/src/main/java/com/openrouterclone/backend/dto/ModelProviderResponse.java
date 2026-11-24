package com.openrouterclone.backend.dto;

import java.util.List;
import java.util.UUID;

public record ModelProviderResponse(
        UUID id,
        String slug,
        String name,
        String providerModelKey,
        List<ModelPricingResponse> pricing) {
}
