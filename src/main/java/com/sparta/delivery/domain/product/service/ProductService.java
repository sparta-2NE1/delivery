package com.sparta.delivery.domain.product.service;

import com.sparta.delivery.config.auth.PrincipalDetails;
import com.sparta.delivery.config.global.exception.custom.*;
import com.sparta.delivery.domain.product.dto.ProductRequestDto;
import com.sparta.delivery.domain.product.dto.ProductResponseDto;
import com.sparta.delivery.domain.product.dto.ProductUpdateRequestDto;
import com.sparta.delivery.domain.product.entity.Product;
import com.sparta.delivery.domain.product.repository.ProductRepository;
import com.sparta.delivery.domain.user.enums.UserRoles;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private static final List<Integer> ALLOWED_PAGE_SIZES = List.of(10, 30, 50);
    private static final int DEFAULT_PAGE_SIZE = 10;

    public ProductResponseDto addProductToStore(UUID storeId, ProductRequestDto productRequestDto, String username) {
        if (productRepository.existsByNameAndStore_StoreIdAndDeletedAtIsNull(productRequestDto.getName(), storeId)) {
            throw new DuplicateProductException("이미 동일한 이름의 상품이 존재합니다.");
        }

        Product product = productRequestDto.toEntity(storeId);
        product.setCreatedBy(username);

        try {
            Product savedProduct = productRepository.save(product);
            return ProductResponseDto.from(savedProduct);
        } catch (Exception e) {
            throw new RuntimeException("상품 등록 중 알 수 없는 오류가 발생했습니다.", e);
        }
    }

    public ProductResponseDto getProduct(UUID productId, PrincipalDetails userDetails) {
        if (userDetails.getRole().equals(UserRoles.ROLE_MASTER) || userDetails.getRole().equals(UserRoles.ROLE_MANAGER)) {
            Product product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException("해당 상품을 찾을 수 없습니다."));
            return ProductResponseDto.from(product);
        }

        if (userDetails.getRole().equals(UserRoles.ROLE_OWNER)) {
            Product product = productRepository.findByProductIdAndDeletedAtIsNull(productId).orElseThrow(() -> new ProductNotFoundException("해당 상품을 찾을 수 없습니다."));
            return ProductResponseDto.from(product);
        }

        Product product = productRepository.findByProductIdAndDeletedAtIsNullAndHiddenFalse(productId).orElseThrow(() -> new ProductNotFoundException("해당 상품을 찾을 수 없습니다."));
        return ProductResponseDto.from(product);
    }

    public Page<ProductResponseDto> getAllProducts(int page, int size, String sortBy, String order, PrincipalDetails userDetails) {
        if (!ALLOWED_PAGE_SIZES.contains(size)) {   // 허용된 페이지 사이즈가 아닌 경우, 기본 페이지 사이즈로 설정
            size = DEFAULT_PAGE_SIZE;
        }

        Sort.Direction direction = order.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        if (userDetails.getRole().equals(UserRoles.ROLE_MASTER) || userDetails.getRole().equals(UserRoles.ROLE_MANAGER)) {
            return productRepository.findAll(pageable).map(ProductResponseDto::from);
        }

        return productRepository.findByDeletedAtIsNullAndHiddenFalse(pageable).map(ProductResponseDto::from);
    }

    @Transactional
    public ProductResponseDto updateProduct(UUID productId, ProductUpdateRequestDto productUpdateRequestDto,  PrincipalDetails userDetails) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException("해당 상품을 찾을 수 없습니다."));

        AuthorizationResult authorizationResult = checkAuthorization(product, userDetails);

        if (!authorizationResult.isStoreOwner && !authorizationResult.hasPermission) {
            throw new UnauthorizedException("상품 수정을 위한 권한이 없습니다.");
        }

        product.update(productUpdateRequestDto);

        return ProductResponseDto.from(product);
    }

    @Transactional
    public ProductResponseDto deleteProduct(UUID productId, PrincipalDetails userDetails) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException("해당 상품을 찾을 수 없습니다."));

        if (product.getDeletedAt() != null) {
            throw new ProductAlreadyDeletedException("이미 삭제된 상품입니다.");
        }

        AuthorizationResult authorizationResult = checkAuthorization(product, userDetails);

        if (!authorizationResult.isStoreOwner && !authorizationResult.hasPermission) {
            throw new UnauthorizedException("상품 삭제를 위한 권한이 없습니다.");
        }

        product.softDelete(userDetails.getUsername());

        return ProductResponseDto.from(product);
    }


    public record AuthorizationResult(boolean isStoreOwner, boolean hasPermission) {}

    public AuthorizationResult checkAuthorization(Product product, PrincipalDetails userDetails) {
        boolean isStoreOwner = product.getStore().getUser().getUsername().equals(userDetails.getUsername());
        boolean isAdmin = userDetails.getRole().equals(UserRoles.ROLE_MASTER) || userDetails.getRole().equals(UserRoles.ROLE_MANAGER);

        return new AuthorizationResult(isStoreOwner, isAdmin);
    }

    public Page<ProductResponseDto> searchProducts(String productName, int page, int size, String sortBy, String order, PrincipalDetails userDetails) {
        if (!ALLOWED_PAGE_SIZES.contains(size)) {   // 허용된 페이지 사이즈가 아닌 경우, 기본 페이지 사이즈로 설정
            size = DEFAULT_PAGE_SIZE;
        }

        Sort.Direction direction = order.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        if (userDetails.getRole().equals(UserRoles.ROLE_MASTER) || userDetails.getRole().equals(UserRoles.ROLE_MANAGER)) {
            return productRepository.findAllByNameContaining(productName, pageable).map(ProductResponseDto::from);
        }

        if (userDetails.getRole().equals(UserRoles.ROLE_OWNER)) {
            return productRepository.findAllByNameContainingAndDeletedAtIsNull(productName, pageable).map(ProductResponseDto::from);
        }

        return productRepository.findAllByNameContainingAndDeletedAtIsNullAndHiddenFalse(productName, pageable).map(ProductResponseDto::from);
    }

    public void updateProductQuantity(Product product, int count) {
        if(product.getQuantity() == 0 && count == -1)
            throw new ProductQuantityNotAllowedException("주문하신 상품이 품절되었습니다.");
        else {
            product.setQuantity(product.getQuantity() + count);
            productRepository.save(product);
        }
    }
}
