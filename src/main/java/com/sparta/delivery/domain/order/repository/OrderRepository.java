package com.sparta.delivery.domain.order.repository;

import com.sparta.delivery.domain.delivery_address.entity.DeliveryAddress;
import com.sparta.delivery.domain.order.entity.Order;
import com.sparta.delivery.domain.store.entity.Stores;
import com.sparta.delivery.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID>, QuerydslPredicateExecutor<Order> {
    Optional<Order> findByOrderIdAndDeletedAtIsNull(UUID orderId);
    Page<Order> findAllByUserAndDeletedAtIsNull(User user, Pageable pageable);
    Page<Order> findAllByStoresAndDeletedAtIsNull(Stores store, Pageable pageable);
    Optional<Order> findByOrderIdAndUserAndDeletedAtIsNull(UUID orderId, User user);

    Page<Order> findByUserAndDeletedAtIsNullAndStoresInAndDeliveryAddressInOrderByCreatedAtAsc(User user, List<Stores> stores, List<DeliveryAddress> deliveryAddresses, Pageable pageable);


}
