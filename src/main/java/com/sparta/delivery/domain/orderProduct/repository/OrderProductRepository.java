package com.sparta.delivery.domain.orderProduct.repository;

import com.sparta.delivery.domain.order.entity.Order;
import com.sparta.delivery.domain.orderProduct.entity.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderProductRepository extends JpaRepository<OrderProduct, UUID> {
    List<OrderProduct> findByOrder(Order order);
}
