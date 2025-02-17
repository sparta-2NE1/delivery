package com.sparta.delivery.domain.product.service;

import com.sparta.delivery.config.global.exception.custom.DuplicateProductException;
import com.sparta.delivery.domain.product.dto.ProductRequestDto;
import com.sparta.delivery.domain.product.dto.ProductResponseDto;
import com.sparta.delivery.domain.product.entity.Product;
import com.sparta.delivery.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ProductResponseDto addProductToStore(UUID storeId, ProductRequestDto productRequestDto, String username) {
        if (productRepository.existsByNameAndStore_StoreId(productRequestDto.getName(), storeId)) {
            throw new DuplicateProductException("이미 동일한 이름의 상품이 존재합니다.");
        }

        Product product = productRequestDto.toEntity(storeId);
        product.setCreatedBy(username);

        try {
            Product savedProduct = productRepository.save(product);
            return ProductResponseDto.from(savedProduct);
        } catch (Exception e) {
            throw new RuntimeException("상품 등록 중 알 수 없는 오류가 발생했습니다.");
        }
    }
}
