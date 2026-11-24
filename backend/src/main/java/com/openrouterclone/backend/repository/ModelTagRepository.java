package com.openrouterclone.backend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.openrouterclone.backend.entity.ModelTagEntity;

public interface ModelTagRepository extends JpaRepository<ModelTagEntity, UUID> {
    List<ModelTagEntity> findAllByOrderByTagAsc();
}
