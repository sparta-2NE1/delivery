package com.sparta.delivery.orderTest;

import com.sparta.delivery.config.PageableConfig;
import com.sparta.delivery.config.auth.PrincipalDetails;
import com.sparta.delivery.config.global.exception.custom.*;
import com.sparta.delivery.domain.delivery_address.entity.DeliveryAddress;
import com.sparta.delivery.domain.delivery_address.repository.DeliveryAddressRepository;
import com.sparta.delivery.domain.order.dto.OrderListResponseDto;
import com.sparta.delivery.domain.order.dto.OrderRequestDto;
import com.sparta.delivery.domain.order.dto.OrderResponseDto;
import com.sparta.delivery.domain.order.dto.OrderStatusRequestDto;
import com.sparta.delivery.domain.order.entity.Order;
import com.sparta.delivery.domain.order.enums.OrderStatus;
import com.sparta.delivery.domain.order.enums.OrderType;
import com.sparta.delivery.domain.order.repository.OrderRepository;
import com.sparta.delivery.domain.order.service.OrderService;
import com.sparta.delivery.domain.orderProduct.entity.OrderProduct;
import com.sparta.delivery.domain.orderProduct.repository.OrderProductRepository;
import com.sparta.delivery.domain.product.entity.Product;
import com.sparta.delivery.domain.product.repository.ProductRepository;
import com.sparta.delivery.domain.store.entity.Stores;
import com.sparta.delivery.domain.store.repository.StoreRepository;
import com.sparta.delivery.domain.user.entity.User;
import com.sparta.delivery.domain.user.enums.UserRoles;
import com.sparta.delivery.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ActiveProfiles("test") // application-test.yml을 기준으로 test 실행 환경을 구성합니다.
@SpringBootTest  // Spring 컨텍스트를 로드하여 통합 테스트 수행
@Transactional  // 각 테스트 후 롤백하여 데이터 정합성 유지
public class OrderServiceTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DeliveryAddressRepository deliveryAddressRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private PageableConfig pageableConfig;

    @Autowired
    private OrderProductRepository orderProductRepository;

    private User customer;
    private User owner;
    private User dummyOwner;
    private DeliveryAddress deliveryAddress;
    private Stores store1;
    private Stores store2;
    private Product product1;
    private Product product2;
    private Product product3;
    private Product product4;
    private Order order;
    private Order deleteOrder;
    private OrderProduct orderProduct;
    private OrderProduct deleteOrderProduct;



    @BeforeEach
    void setUp() {
        customer = User.builder()
                .userId(UUID.randomUUID())
                .email("customer@example.com")
                .password("encodedPassword")
                .username("customer")
                .nickname("customer")
                .role(UserRoles.ROLE_CUSTOMER)
                .deliveryAddresses(new ArrayList<>())
                .build();

        owner = User.builder()
                .userId(UUID.randomUUID())
                .email("owner@example.com")
                .password("encodedPassword")
                .username("owner")
                .nickname("owner")
                .role(UserRoles.ROLE_OWNER)
                .deliveryAddresses(new ArrayList<>())
                .build();

        dummyOwner = User.builder()
                .userId(UUID.randomUUID())
                .email("dummyOwner@example.com")
                .password("encodedPassword")
                .username("dummyOwner")
                .nickname("dummyOwner")
                .role(UserRoles.ROLE_OWNER)
                .deliveryAddresses(new ArrayList<>())
                .build();
        customer = userRepository.save(customer);
        owner = userRepository.save(owner);
        dummyOwner = userRepository.save(dummyOwner);

        deliveryAddress = DeliveryAddress.builder()
                .deliveryAddressId(UUID.randomUUID())
                .deliveryAddress("testHome")
                .deliveryAddressInfo("Gwanghwamun")
                .detailAddress("101")
                .user(customer)
                .build();
        deliveryAddress = deliveryAddressRepository.save(deliveryAddress);

        store1 = Stores.builder()
                .storeId(UUID.randomUUID())
                .name("testStore1")
                .address("Gwanghwamun")
                .status(true)
                .user(owner)
                .build();

        store2 = Stores.builder()
                .storeId(UUID.randomUUID())
                .name("testStore2")
                .address("Gwanghwamun")
                .status(true)
                .user(owner)
                .build();
        store1 = storeRepository.save(store1);
        store2 = storeRepository.save(store2);

        product1 = Product.builder()
                .productId(UUID.randomUUID())
                .store(store1)
                .name("product1")
                .description("yummy~")
                .price(11000)
                .quantity(20)
                .hidden(false)
                .build();

        product2 = Product.builder()
                .productId(UUID.randomUUID())
                .store(store1)
                .name("product2")
                .description("yummy~")
                .price(11000)
                .quantity(1)
                .hidden(false)
                .build();

        product3 = Product.builder()
                .productId(UUID.randomUUID())
                .store(store1)
                .name("product3")
                .description("yummy~")
                .price(11000)
                .quantity(0)
                .hidden(false)
                .build();

        product4 = Product.builder()
                .productId(UUID.randomUUID())
                .store(store2)
                .name("product4")
                .description("yummy~")
                .price(11000)
                .quantity(3)
                .hidden(false)
                .build();

        product1 = productRepository.save(product1);
        product2 = productRepository.save(product2);
        product3 = productRepository.save(product3);
        product4 = productRepository.save(product4);

        order = Order.builder()
                .orderId(UUID.randomUUID())
                .orderTime(LocalDateTime.now())
                .orderType(OrderType.DELIVERY)
                .orderStatus(OrderStatus.ORDER_IN)
                .requirements("test order")
                .stores(store1)
                .user(customer)
                .deliveryAddress(deliveryAddress)
                .build();

        orderProduct = OrderProduct.builder()
                .orderProductId(UUID.randomUUID())
                .order(order)
                .product(product1)
                .build();

        List<OrderProduct> orderProductList = new ArrayList<>();
        orderProductList.add(orderProduct);

        order.setOrderProductList(orderProductList);
        order = orderRepository.save(order);

        deleteOrder = Order.builder()
                .orderId(UUID.randomUUID())
                .orderTime(LocalDateTime.now())
                .orderType(OrderType.PACKAGING)
                .orderStatus(OrderStatus.ORDER_IN)
                .requirements("test order")
                .stores(store1)
                .user(customer)
                .build();

        deleteOrderProduct = OrderProduct.builder()
                .orderProductId(UUID.randomUUID())
                .order(deleteOrder)
                .product(product1)
                .build();

        List<OrderProduct> orderProductList2 = new ArrayList<>();
        orderProductList.add(deleteOrderProduct);

        deleteOrder.setOrderProductList(orderProductList2);
        deleteOrder = orderRepository.save(deleteOrder);
    }

    @Test
    @DisplayName("주문 등록 성공")
    void testCreateOrderSuccess() {
        List<UUID> productList = new ArrayList<>();
        productList.add(product1.getProductId());
        productList.add(product2.getProductId());

        OrderRequestDto orderRequestDto = new OrderRequestDto(
                deliveryAddress.getDeliveryAddressId(),
                store1.getStoreId(),
                productList,
                OrderType.DELIVERY,
                "test order"
                );

        PrincipalDetails principalDetails = mock(PrincipalDetails.class);
        when(principalDetails.getUsername()).thenReturn("customer");
        when(principalDetails.getRole()).thenReturn(UserRoles.ROLE_CUSTOMER);

        Order order = orderService.createOrder(orderRequestDto, principalDetails.getUsername());

        assertNotNull(order);
        assertEquals("test order", order.getRequirements());
        assertEquals(OrderType.DELIVERY, order.getOrderType());
    }

    @Test
    @DisplayName("주문 등록 실패 - 상품을 안 담았을 때")
    void testCreateOrderFailsWhenProductIsZero() {
        List<UUID> productList = new ArrayList<>();

        OrderRequestDto orderRequestDto = new OrderRequestDto(
                deliveryAddress.getDeliveryAddressId(),
                store1.getStoreId(),
                productList,
                OrderType.DELIVERY,
                "test order"
        );

        PrincipalDetails principalDetails = mock(PrincipalDetails.class);
        when(principalDetails.getUsername()).thenReturn("customer");
        when(principalDetails.getRole()).thenReturn(UserRoles.ROLE_CUSTOMER);

        ProductSelectionRequiredException exception = assertThrows(ProductSelectionRequiredException.class, ()-> {
            orderService.createOrder(orderRequestDto, principalDetails.getUsername());
        });
        assertEquals("1개 이상의 상품을 선택해야합니다.", exception.getMessage());
    }

    @Test
    @DisplayName("주문 등록 실패 - 상품이 해당 가게의 것이 아닐 때")
    void testCreateOrderFailsWhenProductIsNotInStore() {
        List<UUID> productList = new ArrayList<>();
        productList.add(product4.getProductId());

        OrderRequestDto orderRequestDto = new OrderRequestDto(
                deliveryAddress.getDeliveryAddressId(),
                store1.getStoreId(),
                productList,
                OrderType.DELIVERY,
                "test order"
        );

        PrincipalDetails principalDetails = mock(PrincipalDetails.class);
        when(principalDetails.getUsername()).thenReturn("customer");
        when(principalDetails.getRole()).thenReturn(UserRoles.ROLE_CUSTOMER);

        NotStoreProductException exception = assertThrows(NotStoreProductException.class, ()-> {
            orderService.createOrder(orderRequestDto, principalDetails.getUsername());
        });
        assertEquals("해당 가게의 상품이 아닙니다.", exception.getMessage());
    }

    @Test
    @DisplayName("주문 등록 실패 - 상품이 품절일 때")
    void testCreateOrderFailsWhenProductSoldOut() {
        List<UUID> productList = new ArrayList<>();
        productList.add(product3.getProductId());

        OrderRequestDto orderRequestDto = new OrderRequestDto(
                deliveryAddress.getDeliveryAddressId(),
                store1.getStoreId(),
                productList,
                OrderType.DELIVERY,
                "test order"
        );

        PrincipalDetails principalDetails = mock(PrincipalDetails.class);
        when(principalDetails.getUsername()).thenReturn("customer");
        when(principalDetails.getRole()).thenReturn(UserRoles.ROLE_CUSTOMER);

        ProductQuantityNotAllowedException exception = assertThrows(ProductQuantityNotAllowedException.class, ()-> {
            orderService.createOrder(orderRequestDto, principalDetails.getUsername());
        });
        assertEquals("주문하신 상품이 품절되었습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("주문 단일 조회 성공")
    void testSearchOrderSuccess() {
        OrderResponseDto resultOrder = orderService.getSingleOrder(order.getOrderId());

        assertNotNull(resultOrder);
        assertEquals(resultOrder.getOrderId(), order.getOrderId());
    }

    @Test
    @DisplayName("주문 단일 조회 실패 - 주문 ID 오류")
    void testSearchOrderFailWhenOrderIdError() {
        UUID invalidAddressId = UUID.randomUUID();

        OrderNotFoundException exception = assertThrows(OrderNotFoundException.class, ()-> {
            orderService.getSingleOrder(invalidAddressId);
        });
        assertEquals("존재하지 않거나 취소된 주문입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("유저 주문 조회 성공")
    void testSearchUserOrderSuccess() {
        for(int i = 0; i < 12; i++) {
            Order dummy = Order.builder()
                    .orderId(UUID.randomUUID())
                    .orderTime(LocalDateTime.now())
                    .orderType(OrderType.DELIVERY)
                    .orderStatus(OrderStatus.ORDER_IN)
                    .requirements("test order" + i)
                    .stores(store1)
                    .user(customer)
                    .deliveryAddress(deliveryAddress)
                    .build();
            orderRepository.save(dummy);
        }

        PrincipalDetails principalDetails = mock(PrincipalDetails.class);
        when(principalDetails.getUsername()).thenReturn("customer");
        when(principalDetails.getRole()).thenReturn(UserRoles.ROLE_CUSTOMER);

        PageRequest pageRequest = pageableConfig.createPageRequest(null, null, null, null);

        List<UUID> storeIdList = Collections.emptyList();
        List<UUID> deliveryAddressIdList = Collections.emptyList();

        Page<OrderListResponseDto> resultList = orderService.getUserOrderList(principalDetails.getUsername(), pageRequest, storeIdList, deliveryAddressIdList);

        assertNotNull(resultList);
        assertEquals(14 , resultList.getTotalElements());
    }

    @Test
    @DisplayName("유저 주문 조회 성공 - 주문 내역 없음")
    void testSearchUserOrderSuccessWhenOrderNotExist() {
        PrincipalDetails principalDetails = mock(PrincipalDetails.class);
        when(principalDetails.getUsername()).thenReturn("owner");
        when(principalDetails.getRole()).thenReturn(UserRoles.ROLE_CUSTOMER);

        PageRequest pageRequest = pageableConfig.createPageRequest(null, null, null, null);

        List<UUID> storeIdList = Collections.emptyList();
        List<UUID> deliveryAddressIdList = Collections.emptyList();

        OrderNotFoundException exception = assertThrows(OrderNotFoundException.class, ()-> {
            orderService.getUserOrderList(principalDetails.getUsername(), pageRequest, storeIdList, deliveryAddressIdList);
        });
        assertEquals("주문 내역이 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("가게 주문 조회 성공")
    void testSearchStoreOrderSuccess() {
        for(int i = 0; i < 12; i++) {
            Order dummy = Order.builder()
                    .orderId(UUID.randomUUID())
                    .orderTime(LocalDateTime.now())
                    .orderType(OrderType.DELIVERY)
                    .orderStatus(OrderStatus.ORDER_IN)
                    .requirements("test order" + i)
                    .stores(store1)
                    .user(customer)
                    .deliveryAddress(deliveryAddress)
                    .build();
            orderRepository.save(dummy);
        }

        PrincipalDetails principalDetails = mock(PrincipalDetails.class);
        when(principalDetails.getUsername()).thenReturn("owner");
        when(principalDetails.getRole()).thenReturn(UserRoles.ROLE_OWNER);

        PageRequest pageRequest = pageableConfig.createPageRequest(null, null, null, null);

        Page<OrderListResponseDto> resultList = orderService.getStoreOrderList(store1.getStoreId(), pageRequest, principalDetails.getUsername());

        assertNotNull(resultList);
        assertEquals(14 , resultList.getTotalElements());

    }

    @Test
    @DisplayName("가게 주문 조회 실패 - 가게의 오너가 아님")
    void testSearchUserOrderFailWhenNotStoreOwner() {
        PrincipalDetails principalDetails = mock(PrincipalDetails.class);
        when(principalDetails.getUsername()).thenReturn("dummyOwner");
        when(principalDetails.getRole()).thenReturn(UserRoles.ROLE_OWNER);

        PageRequest pageRequest = pageableConfig.createPageRequest(null, null, null, null);

        NotStoreOwnerException exception = assertThrows(NotStoreOwnerException.class, ()-> {
            orderService.getStoreOrderList(store1.getStoreId(), pageRequest, principalDetails.getUsername());
        });
        assertEquals("해당 가게의 주인이 아니므로 가게 주문을 조회할 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("가게 주문 조회 성공 - 주문 내역 없음")
    void testSearchStoreOrderSuccessWhenOrderNotExist() {
        PrincipalDetails principalDetails = mock(PrincipalDetails.class);
        when(principalDetails.getUsername()).thenReturn("owner");
        when(principalDetails.getRole()).thenReturn(UserRoles.ROLE_OWNER);

        PageRequest pageRequest = pageableConfig.createPageRequest(null, null, null, null);

        OrderNotFoundException exception = assertThrows(OrderNotFoundException.class, ()-> {
            orderService.getStoreOrderList(store2.getStoreId(), pageRequest, principalDetails.getUsername());
        });
        assertEquals("해당 가게에 존재하는 주문건이 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("주문 수정 성공")
    void testUpdateOrderSuccess() {
        PrincipalDetails principalDetails = mock(PrincipalDetails.class);
        when(principalDetails.getUsername()).thenReturn("customer");
        when(principalDetails.getRole()).thenReturn(UserRoles.ROLE_CUSTOMER);

        List<UUID> productList = new ArrayList<>();
        productList.add(product1.getProductId());
        productList.add(product2.getProductId());

        OrderRequestDto orderRequestDto1 = new OrderRequestDto(
                deliveryAddress.getDeliveryAddressId(),
                store1.getStoreId(),
                productList,
                OrderType.DELIVERY,
                "test order"
        );
        Order firstOrder = orderService.createOrder(orderRequestDto1, principalDetails.getUsername());

        productList.clear();
        productList.add(product1.getProductId());

        OrderRequestDto orderRequestDto2 = new OrderRequestDto(
                deliveryAddress.getDeliveryAddressId(),
                store1.getStoreId(),
                productList,
                OrderType.DELIVERY,
                "test order update"
        );
        OrderResponseDto resultDto = orderService.updateOrder(orderRequestDto2, firstOrder.getOrderId(), principalDetails.getUsername());

        assertNotNull(resultDto);
        assertEquals("test order update", resultDto.getRequirements());
        assertEquals(1, product2.getQuantity());
    }

    @Test
    @DisplayName("주문 수정 실패 - 결제 후 주문 수정할 때")
    void testUpdateOrderFailWhenOrderStatusError() {
        PrincipalDetails principalDetails = mock(PrincipalDetails.class);
        when(principalDetails.getUsername()).thenReturn("customer");
        when(principalDetails.getRole()).thenReturn(UserRoles.ROLE_CUSTOMER);

        List<UUID> productList = new ArrayList<>();
        productList.add(product1.getProductId());
        productList.add(product2.getProductId());

        OrderRequestDto orderRequestDto1 = new OrderRequestDto(
                deliveryAddress.getDeliveryAddressId(),
                store1.getStoreId(),
                productList,
                OrderType.DELIVERY,
                "test order"
        );
        Order firstOrder = orderService.createOrder(orderRequestDto1, principalDetails.getUsername());
        firstOrder.setOrderStatus(OrderStatus.ORDER_COMPLETE);
        orderRepository.save(firstOrder);

        productList.clear();
        productList.add(product1.getProductId());

        OrderRequestDto orderRequestDto2 = new OrderRequestDto(
                deliveryAddress.getDeliveryAddressId(),
                store1.getStoreId(),
                productList,
                OrderType.DELIVERY,
                "test order update"
        );
        OrderModificationNotAllowedException exception = assertThrows(OrderModificationNotAllowedException.class, ()-> {
            orderService.updateOrder(orderRequestDto2, firstOrder.getOrderId(), principalDetails.getUsername());
        });
        assertEquals("결제 이후 주문 변경은 불가능합니다.", exception.getMessage());
    }

    @Test
    @DisplayName("주문 상태 수정 성공")
    void testUpdateOrderStatusSuccess() {
        PrincipalDetails principalDetails = mock(PrincipalDetails.class);
        when(principalDetails.getUsername()).thenReturn("owner");
        when(principalDetails.getRole()).thenReturn(UserRoles.ROLE_OWNER);

        OrderStatusRequestDto orderStatusRequestDto = new OrderStatusRequestDto(OrderStatus.ORDER_COMPLETE);
        OrderResponseDto resultDto = orderService.updateOrderStatus(order.getOrderId(), principalDetails.getUsername(), orderStatusRequestDto);

        assertNotNull(resultDto);
        assertEquals(OrderStatus.ORDER_COMPLETE, resultDto.getOrderStatus());
    }

    @Test
    @DisplayName("주문 상태 수정 실패 - 가게의 오너가 아님")
    void testUpdateOrderStatusFailWhenNotStoreOwner() {
        PrincipalDetails principalDetails = mock(PrincipalDetails.class);
        when(principalDetails.getUsername()).thenReturn("dummyOwner");
        when(principalDetails.getRole()).thenReturn(UserRoles.ROLE_OWNER);

        OrderStatusRequestDto orderStatusRequestDto = new OrderStatusRequestDto(OrderStatus.ORDER_COMPLETE);

        NotStoreOwnerException exception = assertThrows(NotStoreOwnerException.class, ()-> {
            orderService.updateOrderStatus(order.getOrderId(), principalDetails.getUsername(), orderStatusRequestDto);
        });
        assertEquals("해당 가게의 주인이 아니므로 주문 상태를 수정할 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("주문 삭제 성공")
    void testDeleteOrderSuccess() {
        PrincipalDetails principalDetails = mock(PrincipalDetails.class);
        when(principalDetails.getUsername()).thenReturn("customer");
        when(principalDetails.getRole()).thenReturn(UserRoles.ROLE_CUSTOMER);

        Order resultOrder = orderService.deleteOrder(deleteOrder.getOrderId(), principalDetails.getUsername());
        assertNotNull(resultOrder);
        assertEquals(principalDetails.getUsername(), resultOrder.getDeletedBy());
    }

//    @Test
//    @DisplayName("주문 삭제 실패 - 주문 시간으로부터 5분 이상일 때")
//    @org.junit.jupiter.api.Order(17)
//    void testDeleteOrderFailWhenOrderTimeError() {
//        PrincipalDetails principalDetails = mock(PrincipalDetails.class);
//        when(principalDetails.getUsername()).thenReturn("customer");
//        when(principalDetails.getRole()).thenReturn(UserRoles.ROLE_CUSTOMER);
//
//        OrderModificationNotAllowedException exception = assertThrows(OrderModificationNotAllowedException.class, ()-> {
//            orderService.deleteOrder(deleteOrder.getOrderId(), principalDetails.getUsername());
//        });
//        assertEquals("주문 취소 가능 시간이 지났습니다.", exception.getMessage());
//
//
//    }

}
