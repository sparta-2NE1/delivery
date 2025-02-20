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

    public void createOrder(OrderRequestDto requestDto, String username) {
        //주문목록 테이블에 상품 추가 필요
        try {
            User user = getUser(username);
            DeliveryAddress deliveryAddress = getDeliveryAddress(requestDto.getDeliveryAddressId());
            Stores store = getStores(requestDto.getStoreId());
            List<Product> productList = getProductList(requestDto.getProductId());

            Order order = requestDto.toOrder(store, deliveryAddress, user);
            orderRepository.save(order);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        catch (Exception e) {
            throw new RuntimeException("주문 중 알 수 없는 오류가 발생했습니다.");
        }
    }

    public OrderResponseDto getOrder(UUID orderId) {
        Order order = orderRepository.findByOrderIdAndDeletedAtIsNull(orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않거나 취소된 주문입니다."));

        return order.toResponseDto();
    }

    public Page<OrderResponseDto> getUserOrderList(String username, Pageable pageable) {
        try {
            User user = getUser(username);
            Page<Order> userOrderList = orderRepository.findAllByUserAndDeletedAtIsNull(user, pageable);

            if (userOrderList.isEmpty()) {
                throw new IllegalArgumentException("해당 유저에 존재하는 주문이 없습니다.");
            }

            return userOrderList.map(order -> order.toResponseDto());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("유저 주문 목록을 가져오는 중 알 수 없는 오류가 발생했습니다.");
        }
    }

    public Page<OrderResponseDto> getStoreOrderList(UUID storeId, Pageable pageable) {
        try {
            Stores store = getStores(storeId);

            Page<Order> storeOrderList = orderRepository.findAllByStoresAndDeletedAtIsNull(store, pageable);

            if(storeOrderList.isEmpty()) {
                throw new IllegalArgumentException("해당 가게에 존재하는 주문건이 없습니다.");
            }
            return storeOrderList.map(order -> order.toResponseDto());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("가게 주문 목록을 가져오는 중 알 수 없는 오류가 발생했습니다.");
        }
    }

    @Transactional
    public void deleteOrder(UUID orderId, String username) {
        try {
            User user = getUser(username);
            Order order = getUserOrder(orderId, user);

            //주문 시간으로부터 5분 이내일때만 취소 가능
            LocalDateTime now = LocalDateTime.now();
            if(Duration.between(order.getOrderTime(), now).toMinutes() <= Long.valueOf(5)) {
                order.setOrderStatus(OrderStatus.ORDER_CANCEL);
                order.setDeletedAt(now);
                order.setDeletedBy(username);

                orderRepository.save(order);
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("주문 취소 중 알 수 없는 오류가 발생했습니다.");
        }
    }

    @Transactional
    public OrderResponseDto updateOrder(OrderRequestDto requestDto, UUID orderId, String username) {
        try {
            User user = getUser(username);
            DeliveryAddress deliveryAddress = getDeliveryAddress(requestDto.getDeliveryAddressId());
            Stores store = getStores(requestDto.getStoreId());
            List<Product> productList = getProductList(requestDto.getProductId());
            Order order = getUserOrder(orderId, user);

            //결제 전일 때 주문 변경 가능
            //취소 주문건은 위에서 걸러 옴
            LocalDateTime now = LocalDateTime.now();
            if(order.getOrderStatus().equals(OrderStatus.PAYMENT_WAIT))
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

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        catch (Exception e) {
            throw new RuntimeException("주문 수정 중 알 수 없는 오류가 발생했습니다.");
        }
    }

    @Transactional
    public OrderResponseDto updateOrderStatus(UUID orderId, String username, OrderStatusRequestDto requestDto) {
        try {
            User user = getUser(username);
            Order order = getUserOrder(orderId, user);

            order.setOrderStatus(requestDto.getUpdateStatus());
            orderRepository.save(order);

            return order.toResponseDto();

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        catch (Exception e) {
            throw new RuntimeException("주문 상태 변경 중 알 수 없는 오류가 발생했습니다.");
        }
    }

    private Order getUserOrder(UUID orderId, User user) {
        return orderRepository.findByOrderIdAndUserAndDeletedAtIsNull(orderId, user)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저에 존재하지 않거나 취소된 주문입니다."));
    }

    private User getUser(String username) {
        return userRepository.findByUsernameAndDeletedAtIsNull(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
    }

    private DeliveryAddress getDeliveryAddress(UUID deliveryAddressId) {
        return deliveryAddressRepository.findByDeliveryAddressIdAndDeletedAtIsNull(deliveryAddressId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 배달 주소입니다."));
    }

    private Stores getStores(UUID storeId) {
        return storeRepository.findByStoreIdAndDeletedAtIsNull(storeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 가게입니다."));
    }

    private List<Product> getProductList(List<UUID> productIdList) {
        List<Product> productList = new ArrayList<>();
        for(UUID productId : productIdList) {
            Product product = productRepository.findByProductIdAndDeletedAtIsNullAndHiddenFalse(productId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않거나 품절된 상품입니다."));

            productList.add(product);
        }
        return productList;
    }
}
