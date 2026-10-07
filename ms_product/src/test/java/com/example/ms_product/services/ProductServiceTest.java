package com.example.ms_product.services;

import com.example.ms_product.dtos.ProductFilter;
import com.example.ms_product.dtos.ProductRequest;
import com.example.ms_product.dtos.ProductResponse;
import com.example.ms_product.enums.ProductStatus;
import com.example.ms_product.exceptions.ResourceNotFoundException;
import com.example.ms_product.mappers.ProductMapper;
import com.example.ms_product.models.Product;
import com.example.ms_product.pagination.PageResponse;
import com.example.ms_product.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    ProductRepository repository;

    @Spy
    ProductMapper mapper = new ProductMapper();

    @InjectMocks
    ProductService service;

    private Product produtoExistente;
    private ProductRequest requestValido;

    @BeforeEach
    void setup() {
        produtoExistente = new Product();
        produtoExistente.setId(UUID.randomUUID());
        produtoExistente.setName("Notebook");
        produtoExistente.setDescription("Dell Inspiron");
        produtoExistente.setPrice(new BigDecimal("3500.00"));
        produtoExistente.setCategory("Eletrônicos");
        produtoExistente.setStatus(ProductStatus.ACTIVE);
        produtoExistente.setCreatedAt(Instant.now());
        produtoExistente.setUpdatedAt(Instant.now());
        produtoExistente.setDeleted(false);

        requestValido = new ProductRequest(
                "Notebook",
                "Dell Inspiron",
                new BigDecimal("3500.00"),
                "Eletrônicos",
                ProductStatus.ACTIVE
        );
    }

    @Test
    void deveListarProdutosComFiltros() {
        ProductFilter filter = new ProductFilter("Eletrônicos", ProductStatus.ACTIVE, "Note");
        Pageable pageable = PageRequest.of(0, 20);

        Page<Product> page = new PageImpl<>(List.of(produtoExistente), pageable, 1);
        when(repository.search(
                eq(filter.category()),
                eq(filter.status()),
                eq(filter.name()),
                eq(pageable)
        )).thenReturn(page);

        PageResponse<ProductResponse> resp = service.list(filter, pageable);

        assertThat(resp.content()).hasSize(1);
        assertThat(resp.content().get(0).name()).isEqualTo("Notebook");
        assertThat(resp.page()).isZero();
        assertThat(resp.size()).isEqualTo(20);
        assertThat(resp.totalElements()).isEqualTo(1);
        assertThat(resp.totalPages()).isEqualTo(1);
        assertThat(resp.first()).isTrue();
        assertThat(resp.last()).isTrue();

        verify(repository).search(
                filter.category(), filter.status(), filter.name(), pageable
        );
    }

    @Test
    void deveRetornarPaginaVaziaQuandoNaoHouverResultados() {
        ProductFilter filter = new ProductFilter(null, null, null);
        Pageable pageable = PageRequest.of(0, 20);

        when(repository.search(any(), any(), any(), eq(pageable)))
                .thenReturn(Page.empty(pageable));

        PageResponse<ProductResponse> resp = service.list(filter, pageable);

        assertThat(resp.content()).isEmpty();
        assertThat(resp.totalElements()).isZero();
        assertThat(resp.totalPages()).isZero();
    }

    @Test
    void deveBuscarProdutoPorId() {
        UUID id = produtoExistente.getId();
        when(repository.findById(id)).thenReturn(Optional.of(produtoExistente));

        ProductResponse resp = service.findById(id);

        assertThat(resp.id()).isEqualTo(id);
        assertThat(resp.name()).isEqualTo("Notebook");
        assertThat(resp.status()).isEqualTo(ProductStatus.ACTIVE);

        verify(repository).findById(id);
    }

    @Test
    void deveFalharAoBuscarProdutoInexistente() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(id.toString());

        verify(repository).findById(id);
    }

    @Test
    void deveCriarProduto() {
        when(repository.save(any(Product.class))).thenAnswer(i -> {
            Product p = i.getArgument(0);
            p.setId(UUID.randomUUID());
            p.setCreatedAt(Instant.now());
            if (p.getDeleted() == null) p.setDeleted(false);
            if (p.getStatus() == null) p.setStatus(ProductStatus.ACTIVE);
            return p;
        });

        ProductResponse resp = service.create(requestValido);

        assertThat(resp.id()).isNotNull();
        assertThat(resp.name()).isEqualTo("Notebook");
        assertThat(resp.category()).isEqualTo("Eletrônicos");
        assertThat(resp.price()).isEqualByComparingTo(new BigDecimal("3500.00"));
        assertThat(resp.status()).isEqualTo(ProductStatus.ACTIVE);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("Notebook");
        assertThat(captor.getValue().getDeleted()).isFalse();  // agora passa ✅
    }

    @Test
    void deveAtualizarProduto() {
        UUID id = produtoExistente.getId();
        ProductRequest novo = new ProductRequest(
                "Notebook Gamer",
                "Dell G15",
                new BigDecimal("5000.00"),
                "Eletrônicos",
                ProductStatus.INACTIVE
        );

        when(repository.findByIdAndDeletedFalse(id)).thenReturn(Optional.of(produtoExistente));
        when(repository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        ProductResponse resp = service.update(id, novo);

        assertThat(resp.name()).isEqualTo("Notebook Gamer");
        assertThat(resp.price()).isEqualByComparingTo(new BigDecimal("5000.00"));
        assertThat(resp.status()).isEqualTo(ProductStatus.INACTIVE);

        verify(repository).findByIdAndDeletedFalse(id);
        verify(repository).save(any(Product.class));
    }

    @Test
    void deveFalharAoAtualizarProdutoInexistente() {
        UUID id = UUID.randomUUID();
        when(repository.findByIdAndDeletedFalse(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(id, requestValido))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(repository).findByIdAndDeletedFalse(id);
        verify(repository, never()).save(any());
    }

    @Test
    void deveDeletarProdutoFazendoSoftDelete() {
        UUID id = produtoExistente.getId();
        when(repository.findByIdAndDeletedFalse(id)).thenReturn(Optional.of(produtoExistente));
        when(repository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        service.delete(id);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getDeleted()).isTrue();
    }

    @Test
    void deveFalharAoDeletarProdutoInexistente() {
        UUID id = UUID.randomUUID();
        when(repository.findByIdAndDeletedFalse(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(id))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(repository).findByIdAndDeletedFalse(id);
        verify(repository, never()).save(any());
    }
}