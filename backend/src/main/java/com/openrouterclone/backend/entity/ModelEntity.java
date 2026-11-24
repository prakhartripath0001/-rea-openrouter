package com.openrouterclone.backend.entity;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.BatchSize;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "models")
public class ModelEntity extends AuditedEntity {
    @Column(name = "model_key", nullable = false, unique = true, length = 180)
    private String modelKey;

    @Column(nullable = false, length = 180)
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "context_length", nullable = false)
    private Integer contextLength;

    @BatchSize(size = 32)
    @OneToMany(mappedBy = "model")
    private Set<ModelProviderEntity> modelProviders = new HashSet<>();

    @BatchSize(size = 32)
    @OneToMany(mappedBy = "model")
    private Set<ModelModalityEntity> modalities = new HashSet<>();

    @BatchSize(size = 32)
    @OneToMany(mappedBy = "model")
    private Set<SupportedModelParameterEntity> supportedParameters = new HashSet<>();

    @BatchSize(size = 32)
    @OneToMany(mappedBy = "model")
    private Set<ModelTagEntity> tags = new HashSet<>();

    protected ModelEntity() {
    }

    public String getModelKey() { return modelKey; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Integer getContextLength() { return contextLength; }
    public Set<ModelProviderEntity> getModelProviders() { return modelProviders; }
    public Set<ModelModalityEntity> getModalities() { return modalities; }
    public Set<SupportedModelParameterEntity> getSupportedParameters() { return supportedParameters; }
    public Set<ModelTagEntity> getTags() { return tags; }
}
