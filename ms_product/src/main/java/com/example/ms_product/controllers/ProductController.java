package com.example.ms_product.controllers;

import com.example.ms_product.dtos.ProductFilter;
import com.example.ms_product.dtos.ProductRequest;
import com.example.ms_product.dtos.ProductResponse;
import com.example.ms_product.dtos.ApiError;
import com.example.ms_product.enums.ProductStatus;
import com.example.ms_product.pagination.PageResponse;
import com.example.ms_product.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/produtos")
@RequiredArgsConstructor
@Tag(name = "Produtos", description = "CRUD de produtos com filtros e paginação")
public class ProductController {

    private final ProductService service;

    @GetMapping
    @Operation(
            summary = "Lista produtos com paginação e filtros",
            description = "Retorna uma página de produtos. Todos os filtros são opcionais. " +
                    "A ordenação padrão é por createdAt DESC, tamanho 20."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public PageResponse<ProductResponse> list(
            @Parameter(description = "Filtra por categoria exata", example = "Eletrônicos")
            @RequestParam(required = false) String category,

            @Parameter(description = "Filtra por status do produto", example = "ACTIVE")
            @RequestParam(required = false) ProductStatus status,

            @Parameter(description = "Filtra por parte do nome (busca parcial)", example = "fone")
            @RequestParam(required = false) String name,

            @Parameter(hidden = true)
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return service.list(new ProductFilter(category, status, name), pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um produto pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto encontrado"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ProductResponse findById(
            @Parameter(description = "ID do produto", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            @PathVariable UUID id
    ) {
        return service.findById(id);
    }

    @PostMapping
    @Operation(summary = "Cria um novo produto")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Produto criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<ProductResponse> create(
            @Valid @RequestBody ProductRequest req,
            @Parameter(hidden = true) UriComponentsBuilder uri
    ) {
        ProductResponse created = service.create(req);
        URI location = uri.path("/api/produtos/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um produto existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto atualizado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ProductResponse update(
            @Parameter(description = "ID do produto") @PathVariable UUID id,
            @Valid @RequestBody ProductRequest req
    ) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Remove um produto (soft delete)",
            description = "Marca o produto como deletado (deleted = true). Não remove do banco."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Produto removido"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID do produto") @PathVariable UUID id
    ) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}