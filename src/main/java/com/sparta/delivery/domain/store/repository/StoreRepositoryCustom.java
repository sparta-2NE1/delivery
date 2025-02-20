package com.sparta.delivery.domain.store.repository;

import com.sparta.delivery.domain.store.entity.Stores;
import com.sparta.delivery.domain.store.enums.Category;

import java.util.List;

public interface StoreRepositoryCustom {

    List<Stores> findByNameContainingAndCategoryAndDeletedAtIsNull(String name, Category category);

//    List<Stores> findByNameContainingAndCategory(String name, Category category);
    // keyword와 category로 검색하며 deletedAt이 null인 데이터만 조회
//    List<Store> findByNameContainingAndCategoryAndDeletedAtIsNull(String keyword, String category);

    List<Stores> findByCategory(Category category);
}
