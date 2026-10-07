package com.example.ms_product.pagination;

import org.springframework.data.domain.Page;

import java.util.function.Function;

public final class PageMapper {
    private PageMapper() {
    }

    public static <E, T> PageResponse<T> toResponse(
            Page<E> page,
            Function<E, T> mapper
    ) {
        return PageResponse.of(page, mapper);
    }
}
