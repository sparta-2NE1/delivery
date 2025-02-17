package com.sparta.delivery.domain.order.repository;

import com.sparta.delivery.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findAllByStoreId(UUID store_id);
    List<Order> findAllByUserId(Long user_id);

    Optional<Order> findByIdAndDeleted_atIsNull(UUID order_id);

}
