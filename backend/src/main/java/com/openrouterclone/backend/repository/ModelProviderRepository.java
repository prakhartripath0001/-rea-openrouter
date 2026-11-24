package com.openrouterclone.backend.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.openrouterclone.backend.entity.ModelProviderEntity;

public interface ModelProviderRepository extends JpaRepository<ModelProviderEntity, UUID> {
    long countByProvider_Id(UUID providerId);
}
