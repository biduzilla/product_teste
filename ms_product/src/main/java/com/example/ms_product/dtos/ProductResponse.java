package com.example.ms_product.dtos;

import com.example.ms_product.ProductStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String name,
        String description,
        BigDecimal price,
        String category,
        ProductStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}