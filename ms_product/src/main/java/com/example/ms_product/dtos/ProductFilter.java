package com.example.ms_product.dtos;

import com.example.ms_product.ProductStatus;

public record ProductFilter(
        String category,
        ProductStatus status,
        String name
) {}

