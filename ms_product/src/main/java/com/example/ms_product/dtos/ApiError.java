package com.example.ms_product.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.Map;

@Schema(name = "ApiError", description = "Formato padrão de erro retornado pela API")
public record ApiError(

        @Schema(description = "Momento em que o erro ocorreu", example = "2024-05-01T12:00:00Z")
        Instant timestamp,

        @Schema(description = "Código HTTP", example = "404")
        int status,

        @Schema(description = "Tipo curto do erro", example = "Not Found")
        String error,

        @Schema(description = "Mensagem detalhada", example = "Produto não encontrado: 3fa85f64-...")
        String message,

        @Schema(description = "Rota que gerou o erro", example = "/api/produtos/3fa85f64-...")
        String path,

        @Schema(description = "Erros de validação por campo (opcional)")
        Map<String, String> fields
) {
}