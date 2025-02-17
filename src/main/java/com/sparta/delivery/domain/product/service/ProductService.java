package com.sparta.delivery.domain.product.service;

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
        Product product = productRequestDto.toEntity(storeId);
        product.setCreatedBy(username);
        Product savedProduct = productRepository.save(product);
        return ProductResponseDto.from(savedProduct);
    }
}
