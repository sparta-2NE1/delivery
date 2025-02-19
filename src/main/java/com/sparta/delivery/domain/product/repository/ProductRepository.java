package com.sparta.delivery.domain.product.repository;

import com.sparta.delivery.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    boolean existsByNameAndStore_StoreIdAndDeletedAtIsNull(String name, UUID storeId);
}
