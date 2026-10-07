package com.example.ms_product.dtos;

import com.example.ms_product.enums.ProductStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ProductFilter", description = "Filtros opcionais para a listagem de produtos")
public record ProductFilter(

        @Schema(description = "Categoria exata", example = "Eletrônicos")
        String category,

        @Schema(description = "Status do produto", example = "ACTIVE")
        ProductStatus status,

        @Schema(description = "Parte do nome do produto", example = "fone")
        String name
) {
}