package com.openrouterclone.backend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.openrouterclone.backend.entity.SupportedModelParameterEntity;

public interface SupportedModelParameterRepository extends JpaRepository<SupportedModelParameterEntity, UUID> {
    List<SupportedModelParameterEntity> findAllByOrderByParameterNameAsc();
}
