package com.sparta.delivery.domain.region.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.delivery.domain.region.entity.QRegion;
import com.sparta.delivery.domain.region.entity.Region;
import com.sparta.delivery.domain.store.entity.QStores;
import com.sparta.delivery.domain.store.entity.Stores;
import com.sparta.delivery.domain.store.enums.Category;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class RegionRepositoryImpl implements RegionRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;
    private final QRegion region = QRegion.region;

    public List<Region> findByLocalityContainingAndDeletedAtIsNull(String name, String sortBy, String order) {
        JPAQuery<Region> query = jpaQueryFactory.selectFrom(region)
                .where(
                        region.locality.contains(name),
                        region.deletedAt.isNull()
                );
        // 정렬 컬럼과 방향이 유효하다면 동적으로 정렬 적용
        if (sortBy != null && !sortBy.isBlank()) {
            // 정렬 방향 지정 (기본은 DESC)
            Order sortOrder = "asc".equalsIgnoreCase(order) ? Order.ASC : Order.DESC;//querydsl Order, entityX

            // PathBuilder를 이용하여 동적으로 컬럼을 지정
            PathBuilder<Region> entityPath = new PathBuilder<>(Region.class, "region");

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
