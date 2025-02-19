package com.sparta.delivery.domain.delivery_address.repository;

import com.sparta.delivery.domain.delivery_address.entity.DeliveryAddress;
import com.sparta.delivery.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddress , UUID> {

    // 해당 유저가 특정 배송지명을 가진 주소를 가지고있는지 여부를 반환
    boolean existsByUserAndDeliveryAddress(User user, String deliveryAddress);

    // 사용자가 가지고있는 배송지 개수의 수 반환
    long countByUser(User user);
}
