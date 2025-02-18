package com.sparta.delivery.domain.product.controller;

import com.sparta.delivery.config.auth.PrincipalDetails;
import com.sparta.delivery.domain.product.dto.ProductRequestDto;
import com.sparta.delivery.domain.product.dto.ProductResponseDto;
import com.sparta.delivery.domain.product.dto.ProductUpdateRequestDto;
import com.sparta.delivery.domain.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @PostMapping("/stores/{storeId}")
    public ResponseEntity<ProductResponseDto> addProductToStore(@PathVariable UUID storeId, @Valid @RequestBody ProductRequestDto productRequestDto, @AuthenticationPrincipal PrincipalDetails userDetails) {
        ProductResponseDto productResponseDto = productService.addProductToStore(storeId, productRequestDto, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(productResponseDto);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponseDto> getProduct(@PathVariable UUID productId) {
        ProductResponseDto productResponseDto = productService.getProduct(productId);
        return ResponseEntity.ok(productResponseDto);
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponseDto>> getAllProducts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "createdAt") String sortBy, @RequestParam(defaultValue = "desc") String order) {
        Page<ProductResponseDto> allProducts = productService.getAllProducts(page, size, sortBy, order);
        return ResponseEntity.ok(allProducts);
    }

    @PatchMapping("/{productId}")
    public ResponseEntity<ProductResponseDto> updateProduct(@PathVariable UUID productId, @Valid @RequestBody ProductUpdateRequestDto productUpdateRequestDto) {
        ProductResponseDto productResponseDto = productService.updateProduct(productId, productUpdateRequestDto);
        return ResponseEntity.ok(productResponseDto);
    }
}
