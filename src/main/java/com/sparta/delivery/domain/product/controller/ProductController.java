package com.sparta.delivery.domain.product.controller;

import com.sparta.delivery.config.auth.PrincipalDetails;
import com.sparta.delivery.domain.product.dto.ProductRequestDto;
import com.sparta.delivery.domain.product.dto.ProductResponseDto;
import com.sparta.delivery.domain.product.dto.ProductUpdateRequestDto;
import com.sparta.delivery.domain.product.service.ProductService;
import com.sparta.delivery.domain.product.swagger.ProductSwaggerDocs;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name ="Product API", description = "상품 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @ProductSwaggerDocs.AddProductToStore
    @PostMapping("/stores/{storeId}")
    public ResponseEntity<ProductResponseDto> addProductToStore(@PathVariable UUID storeId, @Valid @RequestBody ProductRequestDto productRequestDto, @AuthenticationPrincipal PrincipalDetails userDetails) {
        ProductResponseDto productResponseDto = productService.addProductToStore(storeId, productRequestDto, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(productResponseDto);
    }

    @ProductSwaggerDocs.GetProduct
    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponseDto> getProduct(@PathVariable UUID productId, @AuthenticationPrincipal PrincipalDetails userDetails) {
        ProductResponseDto productResponseDto = productService.getProduct(productId, userDetails);
        return ResponseEntity.ok(productResponseDto);
    }

    @ProductSwaggerDocs.AllProductList
    @GetMapping
    public ResponseEntity<Page<ProductResponseDto>> getAllProducts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "createdAt") String sortBy, @RequestParam(defaultValue = "desc") String order, @AuthenticationPrincipal PrincipalDetails userDetails) {
        Page<ProductResponseDto> allProducts = productService.getAllProducts(page, size, sortBy, order, userDetails);
        return ResponseEntity.ok(allProducts);
    }

    @ProductSwaggerDocs.UpdateProduct
    @PatchMapping("/{productId}")
    public ResponseEntity<ProductResponseDto> updateProduct(@PathVariable UUID productId, @Valid @RequestBody ProductUpdateRequestDto productUpdateRequestDto, @AuthenticationPrincipal PrincipalDetails userDetails) {
        ProductResponseDto productResponseDto = productService.updateProduct(productId, productUpdateRequestDto, userDetails);
        return ResponseEntity.ok(productResponseDto);
    }

    @ProductSwaggerDocs.DeleteProduct
    @PatchMapping("/{productId}/delete")
    public ResponseEntity<ProductResponseDto> deleteProduct(@PathVariable UUID productId, @AuthenticationPrincipal PrincipalDetails userDetails) {
        ProductResponseDto productResponseDto = productService.deleteProduct(productId, userDetails);
        return ResponseEntity.ok(productResponseDto);
    }

    @ProductSwaggerDocs.SearchProduct
    @GetMapping("search/{productName}")
    public ResponseEntity<Page<ProductResponseDto>> searchProduct(@PathVariable String productName, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "createdAt") String sortBy, @RequestParam(defaultValue = "desc") String order, @AuthenticationPrincipal PrincipalDetails userDetails) {
        Page<ProductResponseDto> searchProducts = productService.searchProducts(productName, page, size, sortBy, order, userDetails);
        return ResponseEntity.ok(searchProducts);
    }
}
