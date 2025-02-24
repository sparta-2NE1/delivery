package com.sparta.delivery.domain.store.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.delivery.domain.store.entity.QStores;
import com.sparta.delivery.domain.store.entity.Stores;
import com.sparta.delivery.domain.store.enums.Category;
import lombok.RequiredArgsConstructor;
import java.util.List;

@RequiredArgsConstructor
public class StoreRepositoryImpl implements StoreRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
    private final QStores stores = QStores.stores;

    public List<Stores> findByNameContainingAndCategoryAndDeletedAtIsNull(String name, Category category, String sortBy, String order) {
        JPAQuery<Stores> query = jpaQueryFactory.selectFrom(stores)
                .where(
                        stores.name.contains(name),
                        stores.category.eq(category),
                        stores.deletedAt.isNull()
                );
        // 정렬 컬럼과 방향이 유효하다면 동적으로 정렬 적용
        if (sortBy != null && !sortBy.isBlank()) {
            // 정렬 방향 지정 (기본은 DESC)
            Order sortOrder = "asc".equalsIgnoreCase(order) ? Order.ASC : Order.DESC;//querydsl Order, entityX

            // PathBuilder를 이용하여 동적으로 컬럼을 지정
            PathBuilder<Stores> entityPath = new PathBuilder<>(Stores.class, "stores");

            // Expressions.path()로 동적 정렬에 사용할 Expression 생성
            query.orderBy(
                    new OrderSpecifier<>(
                            sortOrder,
                            Expressions.comparablePath(Comparable.class, entityPath, sortBy)
                    )
            );
        }

        // 결과 조회
        return query.fetch();
    }


    public List<Stores> findByCategoryAndDeletedAtIsNull(Category category, String sortBy, String order) {
        JPAQuery<Stores> query = jpaQueryFactory.selectFrom(stores)
                .where(
                        stores.category.eq(category),
                        stores.deletedAt.isNull()
                );

        if (sortBy != null && !sortBy.isBlank()) {
            // 정렬 방향 지정 (기본은 DESC)
            Order sortOrder = "asc".equalsIgnoreCase(order) ? Order.ASC : Order.DESC;//querydsl Order, entityX

            // PathBuilder를 이용하여 동적으로 컬럼을 지정
            PathBuilder<Stores> entityPath = new PathBuilder<>(Stores.class, "stores");

            // Expressions.path()로 동적 정렬에 사용할 Expression 생성
            query.orderBy(
                    new OrderSpecifier<>(
                            sortOrder,
                            Expressions.comparablePath(Comparable.class, entityPath, sortBy)
                    )
            );
        }

        // 결과 조회
        return query.fetch();
    }

}
