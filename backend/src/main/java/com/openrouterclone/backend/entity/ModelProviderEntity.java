package com.openrouterclone.backend.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "model_providers", uniqueConstraints = {
        @UniqueConstraint(name = "uq_model_provider", columnNames = {"model_id", "provider_id"})
})
public class ModelProviderEntity extends AuditedEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "model_id", nullable = false)
    private ModelEntity model;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "provider_id", nullable = false)
    private ProviderEntity provider;

    @Column(name = "provider_model_key", length = 180)
    private String providerModelKey;

    @OneToMany(mappedBy = "modelProvider")
    private Set<ModelPricingEntity> pricing = new HashSet<>();

    protected ModelProviderEntity() {
    }

    public ModelEntity getModel() { return model; }
    public ProviderEntity getProvider() { return provider; }
    public String getProviderModelKey() { return providerModelKey; }
    public Set<ModelPricingEntity> getPricing() { return pricing; }
}
