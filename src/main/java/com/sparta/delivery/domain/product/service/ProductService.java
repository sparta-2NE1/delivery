package com.sparta.delivery.domain.product.service;

import com.sparta.delivery.config.global.exception.custom.DuplicateProductException;
import com.sparta.delivery.domain.product.dto.ProductRequestDto;
import com.sparta.delivery.domain.product.dto.ProductResponseDto;
import com.sparta.delivery.domain.product.entity.Product;
import com.sparta.delivery.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private static final List<Integer> ALLOWED_PAGE_SIZES = List.of(10, 30, 50);
    private static final int DEFAULT_PAGE_SIZE = 10;

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

    public Page<ProductResponseDto> getAllProducts(int page, int size, String sortBy, String order) {
        if (!ALLOWED_PAGE_SIZES.contains(size)) {   // 허용된 페이지 사이즈가 아닌 경우, 기본 페이지 사이즈로 설정
            size = DEFAULT_PAGE_SIZE;
        }

        Sort.Direction direction = order.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        return productRepository.findAll(pageable).map(ProductResponseDto::from);
    }
}
