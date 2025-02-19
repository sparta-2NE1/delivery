package com.sparta.delivery.domain.delivery_address.repository;

import com.sparta.delivery.domain.delivery_address.entity.DeliveryAddress;
import com.sparta.delivery.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;
import java.util.UUID;

public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddress , UUID>, QuerydslPredicateExecutor<DeliveryAddress> {

    // 해당 유저가 특정 배송지명을 가진 주소를 가지고있는지 여부를 반환
    boolean existsByUserAndDeliveryAddressAndDeletedAtIsNull(User user, String deliveryAddress);

    // 사용자가 가지고있는 배송지 개수의 수 반환
    long countByUserAndDeletedAtIsNull(User user);

    // 제거 되지않은 배송지 반환
    Optional<DeliveryAddress> findByDeliveryAddressIdAndDeletedAtIsNull(UUID deliveryAddressId);
}
