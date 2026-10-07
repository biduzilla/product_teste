package com.example.ms_product.services;

import com.example.ms_product.dtos.ProductFilter;
import com.example.ms_product.dtos.ProductRequest;
import com.example.ms_product.dtos.ProductResponse;
import com.example.ms_product.exceptions.ResourceNotFoundException;
import com.example.ms_product.mappers.ProductMapper;
import com.example.ms_product.models.Product;
import com.example.ms_product.pagination.PageMapper;
import com.example.ms_product.pagination.PageResponse;
import com.example.ms_product.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public PageResponse<ProductResponse> list(
            ProductFilter filter,
            Pageable pageable
    ) {
        log.info("Listando produtos filtro={} page={} size={}",
                filter, pageable.getPageNumber(), pageable.getPageSize());
        Page<Product> page = productRepository.search(
                filter.category(),
                filter.status(),
                filter.name(),
                pageable
        );
        return PageMapper.toResponse(page, productMapper::toResponse);
    }

    public ProductResponse findById(UUID id) {
        log.info("Buscando produto {}", id);
        Product p = productRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado: " + id));
        return productMapper.toResponse(p);
    }

    @Transactional
    public ProductResponse create(ProductRequest req) {
        log.info("Criando produto {}", req);
        Product saved = productRepository.save(productMapper.toEntity(req));
        log.info("Produto criado id={}", saved.getId());
        return productMapper.toResponse(saved);
    }

    @Transactional
    public ProductResponse update(UUID id, ProductRequest req) {
        log.info("Atualizando produto {}", id);
        Product p = productRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado: " + id));

        productMapper.updateEntity(p, req);
        Product updated = productRepository.save(p);
        return productMapper.toResponse(updated);
    }

    @Transactional
    public void delete(UUID id) {
        log.info("Removendo produto id={}", id);
        Product p = productRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado: " + id));
        p.setDeleted(true);
        productRepository.save(p);
    }
}
