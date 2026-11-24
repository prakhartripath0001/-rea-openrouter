package com.openrouterclone.backend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.openrouterclone.backend.entity.ModelModalityEntity;

public interface ModelModalityRepository extends JpaRepository<ModelModalityEntity, UUID> {
    List<ModelModalityEntity> findAllByOrderByModalityAsc();
}
