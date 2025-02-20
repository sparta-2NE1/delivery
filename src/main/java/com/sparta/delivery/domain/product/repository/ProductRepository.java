package com.sparta.delivery.domain.product.repository;

import com.sparta.delivery.domain.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    boolean existsByNameAndStore_StoreIdAndDeletedAtIsNull(String name, UUID storeId);

    Optional<Product> findByProductIdAndDeletedAtIsNullAndHiddenFalse(UUID productId);

    Optional<Product> findByProductIdAndDeletedAtIsNull(UUID productId);

    Page<Product> findAllByNameContaining(String productName, Pageable pageable);

    Page<Product> findAllByNameContainingAndDeletedAtIsNull(String productName, Pageable pageable);

    Page<Product> findAllByNameContainingAndDeletedAtIsNullAndHiddenFalse(String productName, Pageable pageable);
}
