package com.openrouterclone.backend.repository;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.openrouterclone.backend.entity.ModelPricingEntity;

public interface ModelPricingRepository extends JpaRepository<ModelPricingEntity, UUID> {
    @Query("select min(p.promptRatePerMillion) from ModelPricingEntity p")
    BigDecimal findMinimumPromptRate();

    @Query("select max(p.promptRatePerMillion) from ModelPricingEntity p")
    BigDecimal findMaximumPromptRate();

    @Query("select min(p.outputRatePerMillion) from ModelPricingEntity p")
    BigDecimal findMinimumOutputRate();

    @Query("select max(p.outputRatePerMillion) from ModelPricingEntity p")
    BigDecimal findMaximumOutputRate();
}
