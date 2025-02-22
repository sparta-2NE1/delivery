package com.sparta.delivery.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Configuration
public class PageableConfig {

    private static final int defaultPageSize = 10; // 기본 페이지 크기

    private static final int maxPageSize = 50; // 최대 페이지 크기

    private static final String sortBy = "createdAt"; // 기본 정렬 필드

    public Pageable createPageRequest(Integer page, Integer size, String sortBy, String orderBy) {
        int pageNumber = (page != null) ? page : 0;
        int pageSize = (size != null) ? size : defaultPageSize;

        if (pageSize > maxPageSize)
            pageSize = defaultPageSize;

        String sortField = (sortBy != null) ? sortBy : this.sortBy;
        Sort.Direction direction = (orderBy != null && orderBy.equalsIgnoreCase("asc")) ? Sort.Direction.ASC : Sort.Direction.DESC;

        return PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortField));
    }
}

