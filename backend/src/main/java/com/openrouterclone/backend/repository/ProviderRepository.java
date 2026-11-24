package com.openrouterclone.backend.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.openrouterclone.backend.entity.ProviderEntity;

public interface ProviderRepository extends JpaRepository<ProviderEntity, UUID> {
}
