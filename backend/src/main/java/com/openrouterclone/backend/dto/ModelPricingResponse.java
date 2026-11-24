package com.openrouterclone.backend.dto;

import java.math.BigDecimal;

public record ModelPricingResponse(
        String currency,
        String billingUnit,
        BigDecimal promptRatePerMillion,
        BigDecimal outputRatePerMillion,
        boolean free,
        boolean discounted) {
}
