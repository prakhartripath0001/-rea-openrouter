package com.openrouterclone.backend.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "providers")
public class ProviderEntity extends AuditedEntity {
    @Column(nullable = false, unique = true, length = 100)
    private String slug;

    @Column(nullable = false, unique = true, length = 160)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "provider")
    private Set<ModelProviderEntity> modelProviders = new HashSet<>();

    protected ProviderEntity() {
    }

    public String getSlug() { return slug; }
    public String getName() { return name; }
    public String getDescription() { return description; }
}
