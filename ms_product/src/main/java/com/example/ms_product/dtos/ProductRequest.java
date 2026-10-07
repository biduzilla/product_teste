package com.example.ms_product.dtos;

import com.example.ms_product.ProductStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank @Size(min = 3, max = 120) String name,
        @Size(max = 500) String description,
        @NotNull @Positive BigDecimal price,
        @NotBlank @Size(max = 40) String category,
        @NotNull ProductStatus status
) {
}