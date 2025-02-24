package com.sparta.delivery.productTest.unit;

import com.sparta.delivery.config.auth.PrincipalDetails;
import com.sparta.delivery.config.global.exception.custom.DuplicateProductException;
import com.sparta.delivery.config.global.exception.custom.ProductNotFoundException;
import com.sparta.delivery.domain.product.dto.ProductRequestDto;
import com.sparta.delivery.domain.product.dto.ProductResponseDto;
import com.sparta.delivery.domain.product.dto.ProductUpdateRequestDto;
import com.sparta.delivery.domain.product.entity.Product;
import com.sparta.delivery.domain.product.repository.ProductRepository;
import com.sparta.delivery.domain.product.service.ProductService;
import com.sparta.delivery.domain.store.entity.Stores;
import com.sparta.delivery.domain.user.enums.UserRoles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @InjectMocks
    private ProductService productService;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private PrincipalDetails principalDetails;
    private UUID productId;
    private UUID storeId;
    private Product savedProduct;
    private Stores store;
    private ProductRequestDto productRequestDto;
    private ProductUpdateRequestDto productUpdateRequestDto;
    private static final String USERNAME = "TestUser";
    private static final String PRODUCT_NAME = "ProductName";
    private static final String PRODUCT_DESCRIPTION = "Description";
    private static final int PRODUCT_PRICE = 1000;
    private static final int PRODUCT_QUANTITY = 10;
    private static final boolean PRODUCT_HIDDEN = false;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();

        storeId = UUID.randomUUID();

        store = Stores.builder()
                .storeId(storeId)
                .build();

        savedProduct = Product.builder()
                .productId(productId)
                .name(PRODUCT_NAME)
                .description(PRODUCT_DESCRIPTION)
                .price(PRODUCT_PRICE)
                .quantity(PRODUCT_QUANTITY)
                .hidden(PRODUCT_HIDDEN)
                .store(store)
                .build();

        productRequestDto = ProductRequestDto.builder()
                .name(PRODUCT_NAME)
                .description(PRODUCT_DESCRIPTION)
                .price(PRODUCT_PRICE)
                .quantity(PRODUCT_QUANTITY)
                .hidden(PRODUCT_HIDDEN)
                .build();

        productUpdateRequestDto = ProductUpdateRequestDto.builder()
                .name(PRODUCT_NAME)
                .description(PRODUCT_DESCRIPTION)
                .price(PRODUCT_PRICE)
                .quantity(PRODUCT_QUANTITY)
                .hidden(PRODUCT_HIDDEN)
                .build();
    }

    @Nested
    @DisplayName("상품 등록")
    class AddProductToStoreTest {

        @Nested
        @DisplayName("성공")
        class Success {
            @Test
            @DisplayName("스토어에 존재하지 않는 상품이면 상품 등록에 성공한다.")
            void addProductToStoreSuccess() {
                when(productRepository.existsByNameAndStore_StoreIdAndDeletedAtIsNull(anyString(), any(UUID.class))).thenReturn(false);
                when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

                ProductResponseDto productResponseDto = productService.addProductToStore(storeId, productRequestDto, USERNAME);

                assertNotNull(productResponseDto);
                assertEquals(PRODUCT_NAME, productResponseDto.getName());
                verify(productRepository, times(1)).save(any(Product.class));   // save()가 1회 호출됐는지 검증
            }
        }

        @Nested
        @DisplayName("실패")
        class Fail {
            @Test
            @DisplayName("스토어에 이미 존재하는 상품이면 상품 등록에 실패하고, DuplicateProductException을 발생시킨다.")
            void addProductToStoreFailDuplicateProduct() {
                when(productRepository.existsByNameAndStore_StoreIdAndDeletedAtIsNull(anyString(), any(UUID.class))).thenReturn(true);

                DuplicateProductException exception = assertThrows(DuplicateProductException.class, () -> productService.addProductToStore(storeId, productRequestDto, USERNAME));
                assertEquals("이미 동일한 이름의 상품이 존재합니다.", exception.getMessage());
                verify(productRepository, never()).save(any(Product.class));   // save()가 호출되지 않았는지 검증
            }

            @Test
            @DisplayName("상품 등록 중 알 수 없는 오류가 발생하면 RuntimeException을 발생시킨다.")
            void addProductToStoreFailUnknownError() {
                when(productRepository.existsByNameAndStore_StoreIdAndDeletedAtIsNull(anyString(), any(UUID.class))).thenReturn(false);
                when(productRepository.save(any(Product.class))).thenThrow(new RuntimeException("상품 등록 중 알 수 없는 오류가 발생했습니다."));

                RuntimeException exception = assertThrows(RuntimeException.class, () -> productService.addProductToStore(storeId, productRequestDto, USERNAME));
                assertEquals("상품 등록 중 알 수 없는 오류가 발생했습니다.", exception.getMessage());
                verify(productRepository, times(1)).save(any(Product.class));
            }
        }
    }

    @Nested
    @DisplayName("상품 상세 조회")
    class GetProductTest {

        @Nested
        @DisplayName("성공")
        class Success {
            @Test
            @DisplayName("마스터 또는 매니저는 숨김 및 삭제 상품을 포함한 모든 상품을 조회할 수 있다.")
            void getProductSuccessForMasterOrManager() {
                when(productRepository.findById(any(UUID.class))).thenReturn(java.util.Optional.of(savedProduct));
                when(principalDetails.getRole()).thenReturn(UserRoles.ROLE_MASTER);

                ProductResponseDto productResponseDto = productService.getProduct(productId, principalDetails);

                assertNotNull(productResponseDto);
                assertEquals(PRODUCT_NAME, productResponseDto.getName());
                verify(productRepository, times(1)).findById(any(UUID.class));
            }

            @Test
            @DisplayName("오너는 삭제되지 않은 상품을 조회할 수 있다.")
            void getProductSuccessForOwner() {
                when(productRepository.findByProductIdAndDeletedAtIsNull(any(UUID.class))).thenReturn(java.util.Optional.of(savedProduct));
                when(principalDetails.getRole()).thenReturn(UserRoles.ROLE_OWNER);

                ProductResponseDto productResponseDto = productService.getProduct(productId, principalDetails);

                assertNotNull(productResponseDto);
                assertEquals(PRODUCT_NAME, productResponseDto.getName());
                verify(productRepository, times(1)).findByProductIdAndDeletedAtIsNull(any(UUID.class));
            }

            @Test
            @DisplayName("고객은 삭제되지 않은 상품 중 숨김 처리되지 않은 상품을 조회할 수 있다.")
            void getProductSuccessForCustomer() {
                when(productRepository.findByProductIdAndDeletedAtIsNullAndHiddenFalse(any(UUID.class))).thenReturn(java.util.Optional.of(savedProduct));
                when(principalDetails.getRole()).thenReturn(UserRoles.ROLE_CUSTOMER);

                ProductResponseDto productResponseDto = productService.getProduct(productId, principalDetails);

                assertNotNull(productResponseDto);
                assertEquals(PRODUCT_NAME, productResponseDto.getName());
                verify(productRepository, times(1)).findByProductIdAndDeletedAtIsNullAndHiddenFalse(any(UUID.class));
            }
        }

        @Nested
        @DisplayName("실패")
        class Fail {
            @Test
            @DisplayName("해당 상품이 존재하지 않으면 ProductNotFoundException을 발생시킨다.")
            void getProductFailProductNotFound() {
                when(productRepository.findById(any(UUID.class))).thenReturn(java.util.Optional.empty());
                when(principalDetails.getRole()).thenReturn(UserRoles.ROLE_MASTER);

                ProductNotFoundException exception = assertThrows(ProductNotFoundException.class, () -> productService.getProduct(productId, principalDetails));
                assertEquals("해당 상품을 찾을 수 없습니다.", exception.getMessage());
                verify(productRepository, times(1)).findById(any(UUID.class));
            }
        }
    }

    @Nested
    @DisplayName("상품 정보 변경")
    class UpdateProductTest {

        @Nested
        @DisplayName("실패")
        class Fail {
            @Test
            @DisplayName("해당 상품이 존재하지 않으면 ProductNotFoundException을 발생시킨다.")
            void updateProductFailProductNotFound() {
                when(productRepository.findById(any(UUID.class))).thenReturn(java.util.Optional.empty());

                ProductNotFoundException exception = assertThrows(ProductNotFoundException.class, () -> productService.updateProduct(productId, productUpdateRequestDto, principalDetails));
                assertEquals("해당 상품을 찾을 수 없습니다.", exception.getMessage());
                verify(productRepository, times(1)).findById(any(UUID.class));
            }
        }
    }

    @Nested
    @DisplayName("상품 삭제")
    class DeleteProductTest {

        @Nested
        @DisplayName("실패")
        class Fail {
            @Test
            @DisplayName("해당 상품이 존재하지 않으면 ProductNotFoundException을 발생시킨다.")
            void deleteProductFailProductNotFound() {
                when(productRepository.findById(any(UUID.class))).thenReturn(java.util.Optional.empty());

                ProductNotFoundException exception = assertThrows(ProductNotFoundException.class, () -> productService.deleteProduct(productId, principalDetails));
                assertEquals("해당 상품을 찾을 수 없습니다.", exception.getMessage());
                verify(productRepository, times(1)).findById(any(UUID.class));
            }
        }
    }
}
