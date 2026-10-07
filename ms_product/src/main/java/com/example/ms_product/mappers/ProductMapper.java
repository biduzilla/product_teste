package com.example.ms_product.mappers;

import com.example.ms_product.dtos.ProductRequest;
import com.example.ms_product.dtos.ProductResponse;
import com.example.ms_product.models.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    public Product toEntity(ProductRequest req) {
        return Product.builder()
                .name(req.name())
                .description(req.description())
                .price(req.price())
                .category(req.category())
                .status(req.status())
                .build();
    }

    public void updateEntity(Product entity, ProductRequest req) {
        entity.setName(req.name());
        entity.setDescription(req.description());
        entity.setPrice(req.price());
        entity.setCategory(req.category());
        entity.setStatus(req.status());
    }

    public ProductResponse toResponse(Product p) {
        return new ProductResponse(
                p.getId(), p.getName(), p.getDescription(),
                p.getPrice(), p.getCategory(), p.getStatus(),
                p.getCreatedAt(), p.getUpdatedAt()
        );
    }
}
