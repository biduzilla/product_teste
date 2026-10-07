package com.example.ms_product.dtos;

import com.example.ms_product.enums.ProductStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(name = "ProductRequest", description = "Dados para criar ou atualizar um produto")
public record ProductRequest(

        @Schema(description = "Nome do produto", example = "Fone Bluetooth XYZ", minLength = 3, maxLength = 120)
        @NotBlank @Size(min = 3, max = 120)
        String name,

        @Schema(description = "Descrição detalhada do produto", example = "Fone sem fio com cancelamento de ruído", maxLength = 500)
        @Size(max = 500)
        String description,

        @Schema(description = "Preço em reais", example = "199.90", minimum = "0.01")
        @NotNull @Positive
        BigDecimal price,

        @Schema(description = "Categoria do produto", example = "Eletrônicos", maxLength = 40)
        @NotBlank @Size(max = 40)
        String category,

        @Schema(description = "Status do produto", example = "ACTIVE")
        @NotNull
        ProductStatus status
) {
}