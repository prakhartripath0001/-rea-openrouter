package com.openrouterclone.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "supported_model_parameters", uniqueConstraints = {
        @UniqueConstraint(name = "uq_model_parameter", columnNames = {"model_id", "parameter_name"})
})
public class SupportedModelParameterEntity extends AuditedEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "model_id", nullable = false)
    private ModelEntity model;

    @Column(name = "parameter_name", nullable = false, length = 100)
    private String parameterName;

    protected SupportedModelParameterEntity() {
    }

    public String getParameterName() { return parameterName; }
}
