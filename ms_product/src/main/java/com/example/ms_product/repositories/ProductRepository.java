package com.example.ms_product.repositories;

import com.example.ms_product.enums.ProductStatus;
import com.example.ms_product.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    Optional<Product> findByIdAndDeletedFalse(UUID id);

    @Query("""
        select p from Product p
        where p.deleted = false
            and (:category is null or p.category = :category)
            and (:status is null or p.status = :status)
            and (:name is null or lower(p.name) like lower(concat('%', cast(:name as text), '%')))
        """)
    Page<Product> search(
            @Param("category") String category,
            @Param("status") ProductStatus status,
            @Param("name") String name,
            Pageable pageable
    );
}
