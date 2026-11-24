package com.openrouterclone.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "model_modalities", uniqueConstraints = {
        @UniqueConstraint(name = "uq_model_modality", columnNames = {"model_id", "modality"})
})
public class ModelModalityEntity extends AuditedEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "model_id", nullable = false)
    private ModelEntity model;

    @Column(nullable = false, length = 40)
    private String modality;

    protected ModelModalityEntity() {
    }

    public String getModality() { return modality; }
}
