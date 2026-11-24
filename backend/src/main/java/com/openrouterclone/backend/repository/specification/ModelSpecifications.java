package com.openrouterclone.backend.repository.specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

import org.springframework.data.jpa.domain.Specification;

import com.openrouterclone.backend.entity.ModelEntity;
import com.openrouterclone.backend.entity.ModelModalityEntity;
import com.openrouterclone.backend.entity.ModelPricingEntity;
import com.openrouterclone.backend.entity.ModelProviderEntity;
import com.openrouterclone.backend.entity.ModelTagEntity;
import com.openrouterclone.backend.entity.ProviderEntity;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Subquery;

public final class ModelSpecifications {
    private ModelSpecifications() {
    }

    public static Specification<ModelEntity> search(String queryText) {
        if (queryText == null || queryText.isBlank()) return null;
        String pattern = "%" + escapeLike(queryText.trim().toLowerCase(Locale.ROOT)) + "%";
        return (root, query, cb) -> {
            Predicate nameMatch = cb.like(cb.lower(root.get("name")), pattern, '\\');
            Predicate keyMatch = cb.like(cb.lower(root.get("modelKey")), pattern, '\\');
            Predicate descriptionMatch = cb.like(cb.lower(root.get("description")), pattern, '\\');
            Subquery<Integer> providerQuery = query.subquery(Integer.class);
            var modelProvider = providerQuery.from(ModelProviderEntity.class);
            Join<ModelProviderEntity, ProviderEntity> provider = modelProvider.join("provider");
            providerQuery.select(cb.literal(1)).where(
                    cb.equal(modelProvider.get("model"), root),
                    cb.or(cb.like(cb.lower(provider.get("name")), pattern, '\\'),
                            cb.like(cb.lower(provider.get("slug")), pattern, '\\')));
            return cb.or(nameMatch, keyMatch, descriptionMatch, cb.exists(providerQuery));
        };
    }

    public static Specification<ModelEntity> modality(String modality) {
        if (modality == null || modality.isBlank()) return null;
        return (root, query, cb) -> {
            Subquery<Integer> subquery = query.subquery(Integer.class);
            var modalityRow = subquery.from(ModelModalityEntity.class);
            subquery.select(cb.literal(1)).where(
                    cb.equal(modalityRow.get("model"), root),
                    cb.equal(cb.lower(modalityRow.get("modality")), modality.trim().toLowerCase(Locale.ROOT)));
            return cb.exists(subquery);
        };
    }

    public static Specification<ModelEntity> providers(List<String> providerSlugs) {
        if (providerSlugs == null || providerSlugs.isEmpty()) return null;
        List<String> slugs = providerSlugs.stream().filter(s -> s != null && !s.isBlank())
                .map(s -> s.trim().toLowerCase(Locale.ROOT)).toList();
        if (slugs.isEmpty()) return null;
        return (root, query, cb) -> {
            Subquery<Integer> subquery = query.subquery(Integer.class);
            var modelProvider = subquery.from(ModelProviderEntity.class);
            Join<ModelProviderEntity, ProviderEntity> provider = modelProvider.join("provider");
            subquery.select(cb.literal(1)).where(
                    cb.equal(modelProvider.get("model"), root), provider.get("slug").in(slugs));
            return cb.exists(subquery);
        };
    }

    public static Specification<ModelEntity> hasTag(String tag) {
        if (tag == null || tag.isBlank()) return null;
        return (root, query, cb) -> {
            Subquery<Integer> subquery = query.subquery(Integer.class);
            var tagRow = subquery.from(ModelTagEntity.class);
            subquery.select(cb.literal(1)).where(
                    cb.equal(tagRow.get("model"), root),
                    cb.equal(cb.lower(tagRow.get("tag")), tag.trim().toLowerCase(Locale.ROOT)));
            return cb.exists(subquery);
        };
    }

    public static Specification<ModelEntity> minContextLength(Integer value) {
        return value == null ? null : (root, query, cb) -> cb.ge(root.get("contextLength"), value);
    }

    public static Specification<ModelEntity> maxContextLength(Integer value) {
        return value == null ? null : (root, query, cb) -> cb.le(root.get("contextLength"), value);
    }

    public static Specification<ModelEntity> pricing(BigDecimal maxPrompt, BigDecimal maxOutput,
            Boolean freeOnly, Boolean discountedOnly) {
        if (maxPrompt == null && maxOutput == null && !Boolean.TRUE.equals(freeOnly)
                && !Boolean.TRUE.equals(discountedOnly)) return null;
        return (root, query, cb) -> {
            Subquery<Integer> subquery = query.subquery(Integer.class);
            var pricing = subquery.from(ModelPricingEntity.class);
            Join<ModelPricingEntity, ModelProviderEntity> modelProvider = pricing.join("modelProvider");
            var predicates = new java.util.ArrayList<Predicate>();
            predicates.add(cb.equal(modelProvider.get("model"), root));
            if (maxPrompt != null) predicates.add(cb.le(pricing.get("promptRatePerMillion"), maxPrompt));
            if (maxOutput != null) predicates.add(cb.and(
                    cb.isNotNull(pricing.get("outputRatePerMillion")),
                    cb.le(pricing.get("outputRatePerMillion"), maxOutput)));
            if (Boolean.TRUE.equals(freeOnly)) predicates.add(cb.isTrue(pricing.get("free")));
            if (Boolean.TRUE.equals(discountedOnly)) predicates.add(cb.isTrue(pricing.get("discounted")));
            subquery.select(cb.literal(1)).where(predicates.toArray(Predicate[]::new));
            return cb.exists(subquery);
        };
    }

    private static String escapeLike(String value) {
        return value.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }
}
