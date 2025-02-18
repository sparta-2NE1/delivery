package com.sparta.delivery.domain.order.service;

import com.sparta.delivery.domain.delivery_address.entity.DeliveryAddress;
import com.sparta.delivery.domain.delivery_address.repository.DeliveryAddressRepository;
import com.sparta.delivery.domain.order.dto.OrderRequestDto;
import com.sparta.delivery.domain.order.dto.OrderResponseDto;
import com.sparta.delivery.domain.order.dto.OrderStatusRequestDto;
import com.sparta.delivery.domain.order.entity.Order;
import com.sparta.delivery.domain.order.enums.OrderStatus;
import com.sparta.delivery.domain.order.repository.OrderRepository;
import com.sparta.delivery.domain.product.entity.Product;
import com.sparta.delivery.domain.product.repository.ProductRepository;
import com.sparta.delivery.domain.store.entity.Stores;
import com.sparta.delivery.domain.store.repository.StoreRepository;
import com.sparta.delivery.domain.user.entity.User;
import com.sparta.delivery.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final DeliveryAddressRepository deliveryAddressRepository;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;

//    public void createOrder(OrderRequestDto requestDto) {
//        //다른 레파지토리 필요. 추후 구현 예정
//        User user = userRepository.findById(requestDto.getUser_id())
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
//    }

    public OrderResponseDto getOrder(UUID orderId) {
        Order order = orderRepository.findByOrderIdAndDeletedAtIsNull(orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않거나 취소된 주문입니다."));

        
        return order.toResponseDto();
    }

    public Page<OrderResponseDto> getUserOrderList(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        Page<Order> userOrderList = orderRepository.findAllByUserAndDeletedAtIsNull(user, pageable);

        if(userOrderList.isEmpty()) {
            throw new IllegalArgumentException("해당 유저에 존재하는 주문이 없습니다.");
        }

        return userOrderList.map(order -> order.toResponseDto());
    }

    public Page<OrderResponseDto> getStoreOrderList(UUID storeId, Pageable pageable) {
        Stores store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 가게입니다."));

        Page<Order> storeOrderList = orderRepository.findAllByStoresAndDeletedAtIsNull(store, pageable);

        if(storeOrderList.isEmpty()) {
            throw new IllegalArgumentException("해당 가게에 존재하는 주문건이 없습니다.");
        }
        return storeOrderList.map(order -> order.toResponseDto());
    }

    @Transactional
    public OrderResponseDto deleteOrder(UUID orderId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        Order order = orderRepository.findByOrderIdAndUserAndDeletedAtIsNotNull(orderId, user)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저에 존재하지 않거나 취소된 주문입니다."));

        order.setDeletedAt(LocalDateTime.now());
        order.setDeletedBy(username);

        orderRepository.save(order).toResponseDto();

        return order.toResponseDto();
    }

    @Transactional
    public OrderResponseDto updateOrder(OrderRequestDto requestDto, UUID orderId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        Order order = orderRepository.findByOrderIdAndUserAndDeletedAtIsNotNull(orderId, user)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저에 존재하지 않거나 취소된 주문입니다."));

        DeliveryAddress deliveryAddress = deliveryAddressRepository.findById(requestDto.getDeliveryAddressId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 배달 주소입니다."));

        List<Product> updateProductList = new ArrayList<>();
        for(UUID productId : requestDto.getProductId()) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));
        }

        //주문 시간이 현재 시간에서 5분 이하일때 && 결제 전일 때 주문 변경 가능
        //취소 주문건은 위에서 걸러 옴
        LocalDateTime now = LocalDateTime.now();
        if(Duration.between(order.getOrderTime(), now).toMinutes() <= Long.valueOf(5)
        && order.getOrderStatus().equals(OrderStatus.PAYMENT_WAIT))
        {
            order.setUpdatedAt(now);
            order.setUpdatedBy(username);
            order.setOrderType(requestDto.getOrderType());
            order.setRequirements(requestDto.getRequirements());
            order.setDeliveryAddress(deliveryAddress);
            //주문목록 테이블 수정 필요
        }
        else {
            throw new IllegalArgumentException("조건에 맞는 주문이 없습니다.");
        }

        orderRepository.save(order);
        return order.toResponseDto();
    }

    @Transactional
    public OrderResponseDto updateOrderStatus(UUID orderId, String username, OrderStatusRequestDto requestDto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        Order order = orderRepository.findByOrderIdAndUserAndDeletedAtIsNotNull(orderId, user)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저에 존재하지 않거나 취소된 주문입니다."));

        //updateAy, updateby 필요
        order.setOrderStatus(requestDto.getUpdateStatus());
        orderRepository.save(order);

        return order.toResponseDto();
    }
}
