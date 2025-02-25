package com.sparta.delivery.domain.order.service;

import com.querydsl.core.BooleanBuilder;
import com.sparta.delivery.config.global.exception.custom.OrderNotFoundException;
import com.sparta.delivery.config.global.exception.custom.*;
import com.sparta.delivery.domain.delivery_address.entity.DeliveryAddress;
import com.sparta.delivery.domain.delivery_address.repository.DeliveryAddressRepository;
import com.sparta.delivery.domain.order.dto.OrderListResponseDto;
import com.sparta.delivery.domain.order.dto.OrderRequestDto;
import com.sparta.delivery.domain.order.dto.OrderResponseDto;
import com.sparta.delivery.domain.order.dto.OrderStatusRequestDto;
import com.sparta.delivery.domain.order.entity.Order;
import com.sparta.delivery.domain.order.entity.QOrder;
import com.sparta.delivery.domain.order.enums.OrderStatus;
import com.sparta.delivery.domain.order.repository.OrderRepository;
import com.sparta.delivery.domain.orderProduct.entity.OrderProduct;
import com.sparta.delivery.domain.product.entity.Product;
import com.sparta.delivery.domain.product.repository.ProductRepository;
import com.sparta.delivery.domain.product.service.ProductService;
import com.sparta.delivery.domain.store.entity.Stores;
import com.sparta.delivery.domain.store.repository.StoreRepository;
import com.sparta.delivery.domain.user.entity.User;
import com.sparta.delivery.domain.user.enums.UserRoles;
import com.sparta.delivery.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    private final ProductService productService;

    public void createOrder(OrderRequestDto requestDto, String username) {
        try {
            User user = getUser(username);
            DeliveryAddress deliveryAddress = getDeliveryAddress(requestDto.getDeliveryAddressId());
            Stores store = getStores(requestDto.getStoreId());
            List<Product> productList = getProductList(requestDto.getProductId());
            List<OrderProduct> orderProductList = new ArrayList<>();

            Order order = requestDto.toOrder(store, deliveryAddress, user);
            for(Product product : productList) {
                if(product.getStore().getStoreId() != store.getStoreId()) {
                    throw new NotStoreProductException("해당 가게의 상품이 아닙니다.");
                }
                //주문 상품 수량 -1
                productService.updateProductQuantity(product, -1);
                orderProductList.add(new OrderProduct(order, product));
            }
            order.setOrderProductList(orderProductList);
            orderRepository.save(order);
        }
        catch (Exception e) {
            throw e;
        }
    }

    public OrderResponseDto getOrder(UUID orderId) {
        Order order = orderRepository.findByOrderIdAndDeletedAtIsNull(orderId)
                .orElseThrow(() -> new OrderNotFoundException("존재하지 않거나 취소된 주문입니다."));

        return order.toResponseDto();
    }

    public Page<OrderListResponseDto> getUserOrderList(String username, PageRequest pageable, List<UUID> storeIdList, List<UUID> deliveryAddressIdList) {
        try {
            User user = getUser(username);

            List<Stores> storeList = new ArrayList<>();
            for(UUID storeId : storeIdList)
                storeList.add(getStores(storeId));

            List<DeliveryAddress> deliveryAddressList = new ArrayList<>();
            for(UUID deliveryAddressId : deliveryAddressIdList)
                deliveryAddressList.add(getDeliveryAddress(deliveryAddressId));

            QOrder qOrder = QOrder.order;

            BooleanBuilder builder = new BooleanBuilder();
            builder.and(qOrder.user.eq(user));
            builder.and(qOrder.deletedAt.isNull());

            if(!storeList.isEmpty() && storeList != null) {
                builder.and(qOrder.stores.in(storeList));
            }
            if(!deliveryAddressList.isEmpty() && deliveryAddressList != null) {
                builder.and(qOrder.deliveryAddress.in(deliveryAddressList));
            }

            Page<Order> userOrderList = orderRepository.findAll(builder, pageable);

            if(userOrderList.isEmpty()) {
                if(storeList.isEmpty() && deliveryAddressList.isEmpty())
                    throw new OrderNotFoundException("주문 내역이 없습니다.");

                throw new OrderNotFoundException("조건에 해당하는 주문이 없습니다.");
            }

            return userOrderList.map(Order::toResponseListDto);
        } catch (Exception e) {
            throw e;
        }
    }

    public Page<OrderListResponseDto> getStoreOrderList(UUID storeId, Pageable pageable, String username) {
        try {
            User owner  = getUser(username);
            Stores store = getStores(storeId);

            if(owner.getRole() == UserRoles.ROLE_OWNER && !owner.getUserId().equals(store.getUser().getUserId())) {
                throw new NotStoreOwnerException("해당 가게의 주인이 아니므로 가게 주문을 조회할 수 없습니다.");
            }
            Page<Order> storeOrderList = orderRepository.findAllByStoresAndDeletedAtIsNull(store, pageable);

            if(storeOrderList.isEmpty()) {
                throw new OrderNotFoundException("해당 가게에 존재하는 주문건이 없습니다.");
            }
            return storeOrderList.map(Order::toResponseListDto);
        } catch (Exception e) {
            throw e;
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
                //주문 취소 상품 수량 + 1
                for(OrderProduct product : order.getOrderProductList()) {
                    productService.updateProductQuantity(product.getProduct(), 1);
                }

                order.setOrderStatus(OrderStatus.ORDER_CANCEL);
                order.setDeletedAt(now);
                order.setDeletedBy(username);

                orderRepository.save(order);
            }
            else {
                throw new OrderModificationNotAllowedException("주문 취소 가능 시간이 지났습니다.");
            }
        } catch (Exception e) {
            throw e;
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
            if(order.getOrderStatus().equals(OrderStatus.PAYMENT_WAIT))
            {
                order.setOrderType(requestDto.getOrderType());
                order.setRequirements(requestDto.getRequirements());
                order.setDeliveryAddress(deliveryAddress);

                //주문 취소 상품 수량 +1
                for(OrderProduct product : order.getOrderProductList()) {
                    productService.updateProductQuantity(product.getProduct(), 1);
                }

                //주문 상품 수량 -1
                List<OrderProduct> orderProductList = new ArrayList<>();
                for(Product product : productList) {
                    if(product.getStore().getStoreId() != store.getStoreId()) {
                        throw new NotStoreProductException("해당 가게의 상품이 아닙니다.");
                    }

                    productService.updateProductQuantity(product, -1);
                    orderProductList.add(new OrderProduct(order, product));
                }
                order.updateOrderProductList(orderProductList);
            }
            else {
                throw new OrderModificationNotAllowedException("결제 이후 주문 변경은 불가능합니다.");
            }
            orderRepository.save(order);
            return order.toResponseDto();

        }
        catch (Exception e) {
            throw e;
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

        } catch (Exception e) {
            throw e;
        }
    }

    private Order getUserOrder(UUID orderId, User user) {
        return orderRepository.findByOrderIdAndUserAndDeletedAtIsNull(orderId, user)
                .orElseThrow(() -> new UserOrderNotFoundException("해당 유저에 존재하지 않거나 취소된 주문입니다."));
    }

    private User getUser(String username) {
        return userRepository.findByUsernameAndDeletedAtIsNull(username)
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 유저입니다."));
    }

    private DeliveryAddress getDeliveryAddress(UUID deliveryAddressId) {
        return deliveryAddressRepository.findByDeliveryAddressIdAndDeletedAtIsNull(deliveryAddressId)
                .orElseThrow(() -> new DeliveryAddressNotFoundException("존재하지 않는 배달 주소입니다."));
    }

    private Stores getStores(UUID storeId) {
        return storeRepository.findByStoreIdAndDeletedAtIsNull(storeId)
                .orElseThrow(() -> new StoreNotFoundException("존재하지 않는 가게입니다."));
    }

    private List<Product> getProductList(List<UUID> productIdList) {
        List<Product> productList = new ArrayList<>();
        if(productIdList.size() == 0) {
            throw new ProductSelectionRequiredException ("1개 이상의 상품을 선택해야합니다.");
        }
        else {
            for (UUID productId : productIdList) {
                Product product = productRepository.findByProductIdAndDeletedAtIsNullAndHiddenFalse(productId)
                        .orElseThrow(() -> new ProductNotFoundException("존재하지 않거나 품절된 상품입니다."));

                productList.add(product);
            }
        }
        return productList;
    }
}
