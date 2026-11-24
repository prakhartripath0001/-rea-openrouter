package com.openrouterclone.backend.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.openrouterclone.backend.entity.ModelEntity;

public interface ModelRepository extends JpaRepository<ModelEntity, UUID>, JpaSpecificationExecutor<ModelEntity> {
    @Query("select min(m.contextLength) from ModelEntity m")
    Integer findMinimumContextLength();

    @Query("select max(m.contextLength) from ModelEntity m")
    Integer findMaximumContextLength();
}
