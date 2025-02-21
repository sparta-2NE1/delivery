package com.sparta.delivery.domain.store.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.delivery.domain.store.entity.Stores;
import com.sparta.delivery.domain.store.enums.Category;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.sparta.delivery.domain.store.entity.QStores.stores;

@RequiredArgsConstructor
public class StoreRepositoryImpl implements StoreRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public List<Stores> findByNameContainingAndCategoryAndDeletedAtIsNull(String name, Category category) {
        return jpaQueryFactory.selectFrom(stores).where(
                stores.name.contains(name),  // LIKE '%name%'
                stores.category.eq(category), // Category 일치하면
                stores.deletedAt.isNull() // deletedAt이 null이아닌값
        ).fetch(); // 리스트로반환
    }

    public List<Stores> findByCategory(Category category) {
        return jpaQueryFactory.selectFrom(stores).where(
                stores.category.eq(category) //Category가 일치하면
        ).fetch();
    }

}
