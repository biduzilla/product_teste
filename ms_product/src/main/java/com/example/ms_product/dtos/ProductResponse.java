package com.example.ms_product.dtos;

import com.example.ms_product.enums.ProductStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Schema(name = "ProductResponse", description = "Representação de um produto retornado pela API")
public record ProductResponse(

        @Schema(description = "ID único do produto", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,

        @Schema(description = "Nome do produto", example = "Fone Bluetooth XYZ")
        String name,

        @Schema(description = "Descrição do produto", example = "Fone sem fio com cancelamento de ruído")
        String description,

        @Schema(description = "Preço em reais", example = "199.90")
        BigDecimal price,

        @Schema(description = "Categoria", example = "Eletrônicos")
        String category,

        @Schema(description = "Status atual", example = "ACTIVE")
        ProductStatus status,

        @Schema(description = "Data de criação", example = "2024-05-01T12:00:00Z")
        Instant createdAt,

        @Schema(description = "Data da última atualização", example = "2024-05-02T09:30:00Z")
        Instant updatedAt
) {
}