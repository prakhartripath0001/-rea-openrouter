package com.openrouterclone.backend.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "model_pricing")
public class ModelPricingEntity extends AuditedEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "model_provider_id", nullable = false)
    private ModelProviderEntity modelProvider;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(name = "billing_unit", nullable = false, length = 40)
    private String billingUnit;

    @Column(name = "prompt_rate_per_million", nullable = false, precision = 18, scale = 6)
    private BigDecimal promptRatePerMillion;

    @Column(name = "output_rate_per_million", precision = 18, scale = 6)
    private BigDecimal outputRatePerMillion;

    @Column(name = "is_free", nullable = false)
    private boolean free;

    @Column(name = "is_discounted", nullable = false)
    private boolean discounted;

    protected ModelPricingEntity() {
    }

    public ModelProviderEntity getModelProvider() { return modelProvider; }
    public String getCurrency() { return currency; }
    public String getBillingUnit() { return billingUnit; }
    public BigDecimal getPromptRatePerMillion() { return promptRatePerMillion; }
    public BigDecimal getOutputRatePerMillion() { return outputRatePerMillion; }
    public boolean isFree() { return free; }
    public boolean isDiscounted() { return discounted; }
}
