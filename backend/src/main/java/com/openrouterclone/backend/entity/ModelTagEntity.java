package com.openrouterclone.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "model_tags", uniqueConstraints = {
        @UniqueConstraint(name = "uq_model_tag", columnNames = {"model_id", "tag"})
})
public class ModelTagEntity extends AuditedEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "model_id", nullable = false)
    private ModelEntity model;

    @Column(nullable = false, length = 80)
    private String tag;

    protected ModelTagEntity() {
    }

    public String getTag() { return tag; }
}
